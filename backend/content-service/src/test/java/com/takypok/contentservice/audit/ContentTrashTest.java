package com.takypok.contentservice.audit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * The Trash is SQL (init-schema.sql: content_trash and its functions), so it is tested against a
 * real PostgreSQL, like the audit trigger. Needs a reachable server (DB_HOST/DB_PORT/DB_USER/
 * DB_PASSWORD, defaulting to the local dev database); without one every test here is skipped.
 */
class ContentTrashTest {
  private static final String TEST_DB = "content_trash_test";
  private static final String ADA = "'{\"sub\":\"ada\",\"name\":\"Ada\"}'::jsonb";
  private static final String BOB = "'{\"sub\":\"bob\",\"name\":\"Bob\"}'::jsonb";

  private static Connection admin;
  private static Connection db;

  @BeforeAll
  static void createDatabase() throws Exception {
    String host = env("DB_HOST", "127.0.0.1");
    String port = env("DB_PORT", "5432");
    String user = env("DB_USER", "postgres");
    String password = env("DB_PASSWORD", "postgres");
    String base = "jdbc:postgresql://" + host + ":" + port + "/";
    try {
      DriverManager.setLoginTimeout(3);
      admin = DriverManager.getConnection(base + "postgres", user, password);
    } catch (SQLException e) {
      assumeTrue(false, "No PostgreSQL reachable for the Trash test: " + e.getMessage());
    }
    admin.setAutoCommit(true);
    try (Statement s = admin.createStatement()) {
      s.execute("DROP DATABASE IF EXISTS " + TEST_DB + " WITH (FORCE)");
      s.execute("CREATE DATABASE " + TEST_DB);
    }
    db = DriverManager.getConnection(base + TEST_DB, user, password);
    db.setAutoCommit(true);
    try (InputStream schema =
            ContentTrashTest.class
                .getClassLoader()
                .getResourceAsStream("db/changelog/init/init-schema.sql");
        Statement s = db.createStatement()) {
      s.execute(new String(schema.readAllBytes(), StandardCharsets.UTF_8));
      s.execute("INSERT INTO site (code, subdomain) VALUES ('en', 'localhost'), ('vi', 'vn')");
    }
  }

  @AfterAll
  static void dropDatabase() throws Exception {
    if (db != null) db.close();
    if (admin != null) {
      try (Statement s = admin.createStatement()) {
        s.execute("DROP DATABASE IF EXISTS " + TEST_DB + " WITH (FORCE)");
      }
      admin.close();
    }
  }

  private static String env(String name, String fallback) {
    String value = System.getenv(name);
    return value != null && !value.isBlank() ? value : fallback;
  }

  // ---- helpers --------------------------------------------------------------------------------

  private void exec(String sql) throws SQLException {
    try (Statement s = db.createStatement()) {
      s.execute(sql);
    }
  }

  private String scalar(String sql) throws SQLException {
    try (Statement s = db.createStatement();
        ResultSet rs = s.executeQuery(sql)) {
      rs.next();
      return rs.getString(1);
    }
  }

  /** A post as the application would write it (stamped with a user), in site 1. */
  private long post(String slug, String status) throws SQLException {
    return Long.parseLong(
        scalar(
            "INSERT INTO content_item (site_id, content_type, data, status, publish_at, version,"
                + " modified_by, modified_at, created_by, created_at) VALUES (1, 'post',"
                + " '{\"title\":\"T "
                + slug
                + "\",\"slug\":\""
                + slug
                + "\"}', '"
                + status
                + "', "
                + ("SCHEDULED".equals(status) ? "now() + interval '1 day'" : "NULL")
                + ", 3, '{\"sub\":\"eve\",\"name\":\"Eve\"}', now(), '{\"sub\":\"eve\"}', now())"
                + " RETURNING id"));
  }

  private boolean trash(long id, String actor) throws SQLException {
    return "t".equals(scalar("SELECT trash_content_item(" + id + ", 1, " + actor + ")"));
  }

  private String restore(long id, String actor, String slug) throws SQLException {
    return scalar(
        "SELECT restore_trashed_item("
            + id
            + ", 1, "
            + actor
            + ", "
            + (slug == null ? "NULL" : "'" + slug + "'")
            + ")");
  }

  private List<String> trail(long id) throws SQLException {
    List<String> rows = new ArrayList<>();
    try (Statement s = db.createStatement();
        ResultSet rs =
            s.executeQuery(
                "SELECT action || ' | ' || coalesce(actor_name, '') FROM content_audit"
                    + " WHERE item_id = "
                    + id
                    + " ORDER BY id")) {
      while (rs.next()) rows.add(rs.getString(1));
    }
    return rows;
  }

  private String slugs(long id) throws SQLException {
    return scalar(
        "SELECT coalesce(string_agg(slug || CASE WHEN is_primary THEN '*' ELSE '' END, ','"
            + " ORDER BY slug), '') FROM content_slug WHERE item_id = "
            + id);
  }

  // ---- the behaviour --------------------------------------------------------------------------

  @Test
  void trashingMovesTheItemOutOfContentItemAndFreesItsSlug() throws Exception {
    long id = post("moves-out", "DRAFT");

    assertTrue(trash(id, ADA));

    assertEquals("0", scalar("SELECT count(*) FROM content_item WHERE id = " + id));
    assertEquals("1", scalar("SELECT count(*) FROM content_trash WHERE id = " + id));
    assertEquals("", slugs(id));
    assertEquals("T moves-out", scalar("SELECT title FROM content_trash WHERE id = " + id));
    assertEquals("ada", scalar("SELECT trashed_by ->> 'sub' FROM content_trash WHERE id = " + id));
    // the address is free: another post can take it while this one is in the Trash
    post("moves-out", "DRAFT");
  }

  @Test
  void trashingIsRecordedAsTrashByTheActorNotTheLastEditor() throws Exception {
    long id = post("trail-trash", "DRAFT");
    exec("DELETE FROM content_audit WHERE item_id = " + id);

    trash(id, ADA);

    assertEquals(List.of("TRASH | Ada (ada)"), trail(id));
    assertEquals("", scalar("SELECT coalesce(current_setting('cms.audit_action', true), '')"));
  }

  @Test
  void anItemOfAnotherSiteOrAMissingOneIsNotTrashed() throws Exception {
    long id = post("other-site", "DRAFT");

    assertFalse("t".equals(scalar("SELECT trash_content_item(" + id + ", 2, " + ADA + ")")));
    assertFalse(trash(987654321L, ADA));
    assertEquals("1", scalar("SELECT count(*) FROM content_item WHERE id = " + id));
  }

  @Test
  void restoringBringsBackADraftWithTheSameIdAndNoScheduleAndRecordsUntrash() throws Exception {
    long id = post("comes-back", "SCHEDULED");
    exec("DELETE FROM content_audit WHERE item_id = " + id);
    trash(id, ADA);

    String result = restore(id, BOB, null);

    assertTrue(result.contains("\"slug\": \"comes-back\""), result);
    assertEquals(
        "DRAFT|true|true|4|bob",
        scalar(
            "SELECT status || '|' || (publish_at IS NULL) || '|' || (unpublish_at IS NULL) || '|'"
                + " || version || '|' || (modified_by ->> 'sub') FROM content_item WHERE id = "
                + id));
    assertEquals("eve", scalar("SELECT created_by ->> 'sub' FROM content_item WHERE id = " + id));
    assertEquals("comes-back*", slugs(id));
    assertEquals("0", scalar("SELECT count(*) FROM content_trash WHERE id = " + id));
    assertEquals(List.of("TRASH | Ada (ada)", "UNTRASH | Bob (bob)"), trail(id));
  }

  @Test
  void oldAddressesComeBackWhenStillFreeAndAreSkippedWhenTaken() throws Exception {
    long id = post("first-name", "PUBLISHED");
    exec(
        "UPDATE content_item SET data = jsonb_set(data, '{slug}', '\"second-name\"'),"
            + " modified_by = '{\"sub\":\"eve\"}', modified_at = now() + interval '1 second'"
            + " WHERE id = "
            + id);
    exec(
        "UPDATE content_item SET data = jsonb_set(data, '{slug}', '\"third-name\"'),"
            + " modified_by = '{\"sub\":\"eve\"}', modified_at = now() + interval '2 second'"
            + " WHERE id = "
            + id);
    assertEquals("first-name,second-name,third-name*", slugs(id));
    trash(id, ADA);
    post("second-name", "DRAFT"); // takes one of the old addresses meanwhile

    String result = restore(id, ADA, null);

    assertTrue(result.contains("\"skippedAliases\": 1"), result);
    assertEquals("first-name,third-name*", slugs(id));
  }

  @Test
  void restoringIntoATakenSlugFailsAndLeavesTheTrashAloneUnlessAnotherSlugIsGiven()
      throws Exception {
    long id = post("contested", "DRAFT");
    trash(id, ADA);
    long other = post("contested", "DRAFT");

    SQLException ex = assertThrows(SQLException.class, () -> restore(id, ADA, null));
    assertEquals("23505", ex.getSQLState());
    assertEquals("1", scalar("SELECT count(*) FROM content_trash WHERE id = " + id));
    assertEquals("0", scalar("SELECT count(*) FROM content_item WHERE id = " + id));

    restore(id, ADA, "contested-2");

    assertEquals("contested-2*", slugs(id));
    assertEquals(
        "contested-2", scalar("SELECT data ->> 'slug' FROM content_item WHERE id = " + id));
    assertEquals("contested*", slugs(other));
  }

  @Test
  void deletingForGoodRemovesItAndRecordsADeleteByTheActor() throws Exception {
    long id = post("gone-for-good", "DRAFT");
    long mine = post("not-mine", "DRAFT");
    exec("DELETE FROM content_audit WHERE item_id IN (" + id + "," + mine + ")");
    trash(id, ADA);
    trash(mine, ADA);

    // site 2 cannot delete site 1's items; site 1 can
    assertEquals("0", scalar("SELECT delete_trashed_items(ARRAY[" + id + "], 2, " + BOB + ")"));
    assertEquals("1", scalar("SELECT delete_trashed_items(ARRAY[" + id + "], 1, " + BOB + ")"));

    assertEquals("0", scalar("SELECT count(*) FROM content_trash WHERE id = " + id));
    assertEquals("1", scalar("SELECT count(*) FROM content_trash WHERE id = " + mine));
    assertEquals(List.of("TRASH | Ada (ada)", "DELETE | Bob (bob)"), trail(id));
    assertEquals(
        "T gone-for-good",
        scalar("SELECT title FROM content_audit WHERE item_id = " + id + " AND action = 'DELETE'"));
  }

  @Test
  void thePurgeDeletesOnlyWhatIsOlderThanThirtyDaysAsSystem() throws Exception {
    long old = post("purge-old", "DRAFT");
    long fresh = post("purge-fresh", "DRAFT");
    trash(old, ADA);
    trash(fresh, ADA);
    exec("UPDATE content_trash SET trashed_at = now() - interval '31 days' WHERE id = " + old);
    exec("UPDATE content_trash SET trashed_at = now() - interval '29 days' WHERE id = " + fresh);

    assertEquals("1", scalar("SELECT purge_trashed_content()"));

    assertEquals("0", scalar("SELECT count(*) FROM content_trash WHERE id = " + old));
    assertEquals("1", scalar("SELECT count(*) FROM content_trash WHERE id = " + fresh));
    assertTrue(trail(old).contains("DELETE | System"), trail(old).toString());
    assertEquals("0", scalar("SELECT purge_trashed_content()"));
  }

  /**
   * restore_trashed_item lists the columns it puts back. A column added to content_item without
   * being added there would be silently lost on a restore, so this fails first.
   */
  @Test
  void contentItemHasNoColumnTheRestoreDoesNotKnow() throws Exception {
    assertEquals(
        "content_type,created_at,created_by,data,display_order,id,modified_at,modified_by,"
            + "publish_at,search_body,search_meta,search_title,search_vec,site_id,status,"
            + "unpublish_at,version",
        scalar(
            "SELECT string_agg(column_name, ',' ORDER BY column_name) FROM information_schema.columns"
                + " WHERE table_name = 'content_item'"));
  }
}
