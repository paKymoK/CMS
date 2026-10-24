package com.takypok.contentservice.audit;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
 * The audit trigger is SQL (init-schema.sql), so it is tested against a real PostgreSQL: the whole
 * schema is loaded into a throwaway database and written to the way the application, and a person
 * with psql, would. Needs a reachable server (DB_HOST/DB_PORT/DB_USER/DB_PASSWORD, defaulting to
 * the local dev database); without one every test here is skipped rather than failed.
 */
class ContentAuditTriggerTest {
  private static final String TEST_DB = "content_audit_trigger_test";

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
      assumeTrue(false, "No PostgreSQL reachable for the audit trigger test: " + e.getMessage());
    }
    admin.setAutoCommit(true);
    try (Statement s = admin.createStatement()) {
      s.execute("DROP DATABASE IF EXISTS " + TEST_DB + " WITH (FORCE)");
      s.execute("CREATE DATABASE " + TEST_DB);
    }
    db = DriverManager.getConnection(base + TEST_DB, user, password);
    db.setAutoCommit(true);
    try (InputStream schema =
            ContentAuditTriggerTest.class
                .getClassLoader()
                .getResourceAsStream("db/changelog/init/init-schema.sql");
        Statement s = db.createStatement()) {
      s.execute(new String(schema.readAllBytes(), StandardCharsets.UTF_8));
      s.execute("INSERT INTO site (code, subdomain) VALUES ('en', 'localhost')");
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

  /** Inserts a row stamped as the application would (modified_by/at set) and returns its id. */
  private long insertAsApp(String type, String data, String userSub) throws SQLException {
    return insertReturningId(
        "INSERT INTO content_item (site_id, content_type, data, modified_by, modified_at)"
            + " VALUES (1, '"
            + type
            + "', '"
            + data
            + "', '{\"sub\":\""
            + userSub
            + "\",\"name\":null}', now()) RETURNING id");
  }

  private long insertReturningId(String sql) throws SQLException {
    try (Statement s = db.createStatement();
        ResultSet rs = s.executeQuery(sql)) {
      rs.next();
      return rs.getLong(1);
    }
  }

  /** An application-style update: new modified_by and a later modified_at, as Spring stamps. */
  private void updateAsApp(long id, String setClause, String userSub) throws SQLException {
    exec(
        "UPDATE content_item SET "
            + setClause
            + ", version = version + 1, modified_by = '{\"sub\":\""
            + userSub
            + "\"}', modified_at = clock_timestamp() + interval '1 second' WHERE id = "
            + id);
  }

  /** The item's trail, oldest first, as "ACTION | actor | changed fields". */
  private List<String> trail(long id) throws SQLException {
    List<String> rows = new ArrayList<>();
    try (Statement s = db.createStatement();
        ResultSet rs =
            s.executeQuery(
                "SELECT action || ' | ' || coalesce(actor_name, '') || ' | ' || changed::text"
                    + " FROM content_audit WHERE item_id = "
                    + id
                    + " ORDER BY id")) {
      while (rs.next()) rows.add(rs.getString(1));
    }
    return rows;
  }

  private String scalar(String sql) throws SQLException {
    try (Statement s = db.createStatement();
        ResultSet rs = s.executeQuery(sql)) {
      rs.next();
      return rs.getString(1);
    }
  }

  // ---- the behaviour --------------------------------------------------------------------------

  @Test
  void anApplicationCreateIsRecordedAsCreateByTheUserWithEveryFieldListed() throws Exception {
    long id = insertAsApp("post", "{\"title\":\"Hello\",\"slug\":\"hello\"}", "admin");

    assertEquals(
        List.of(
            "CREATE | admin | [\"slug\", \"title\", \"contentType\", \"displayOrder\", \"status\"]"),
        trail(id));
  }

  @Test
  void anUpdateRecordsTheEditorAndOnlyTheFieldsThatChanged() throws Exception {
    long id = insertAsApp("post", "{\"title\":\"Old\",\"body\":\"same\"}", "admin");

    updateAsApp(id, "data = jsonb_set(data, '{title}', '\"New\"')", "jane");

    assertEquals("UPDATE | jane | [\"title\"]", trail(id).get(1));
    String after =
        scalar(
            "SELECT after_data -> 'data' ->> 'title' FROM content_audit WHERE item_id = "
                + id
                + " AND action = 'UPDATE'");
    String before =
        scalar(
            "SELECT before_data -> 'data' ->> 'title' FROM content_audit WHERE item_id = "
                + id
                + " AND action = 'UPDATE'");
    assertEquals("New", after);
    assertEquals("Old", before);
  }

  @Test
  void statusMovesArePublishAndUnpublish() throws Exception {
    long id = insertAsApp("post", "{\"title\":\"T\"}", "admin");

    updateAsApp(id, "status = 'PUBLISHED'", "admin");
    updateAsApp(id, "status = 'DRAFT'", "admin");

    List<String> trail = trail(id);
    assertEquals("PUBLISH | admin | [\"status\"]", trail.get(1));
    assertEquals("UNPUBLISH | admin | [\"status\"]", trail.get(2));
  }

  @Test
  void savesThatChangeNothingAnEditorCanSeeRecordNothing() throws Exception {
    long id = insertAsApp("post", "{\"title\":\"T\"}", "admin");

    // A re-save: only the version counter and the modified stamps move.
    updateAsApp(id, "display_order = display_order", "bob");
    // Validation adding null fields and an object of nulls.
    updateAsApp(id, "data = data || '{\"avatar\":null,\"nested\":{\"x\":null}}'", "bob");
    // The startup backfill filling the search text.
    exec(
        "UPDATE content_item SET search_title = 'x', search_meta = 'y', search_body = 'z' WHERE id = "
            + id);

    assertEquals(1, trail(id).size(), "only the create should be on the trail");
  }

  @Test
  void plainSqlIsRecordedAsADirectDatabaseChange() throws Exception {
    long id = insertAsApp("post", "{\"title\":\"T\"}", "admin");

    // An update that leaves modified_by/modified_at alone: not from the application.
    exec(
        "UPDATE content_item SET data = jsonb_set(data, '{title}', '\"By hand\"') WHERE id = "
            + id);
    // An insert with no modified_by.
    long handmade =
        insertReturningId(
            "INSERT INTO content_item (site_id, content_type, data) VALUES (1, 'stat', '{\"label\":\"Hand\"}') RETURNING id");
    // A delete with no re-save first.
    exec("DELETE FROM content_item WHERE id = " + handmade);

    assertTrue(
        trail(id).get(1).startsWith("UPDATE | Direct database change"), trail(id).toString());
    List<String> handmadeTrail = trail(handmade);
    assertTrue(handmadeTrail.get(0).startsWith("CREATE | Direct database change"));
    assertTrue(handmadeTrail.get(1).startsWith("DELETE | Direct database change"));
  }

  @Test
  void theRestoreHintMakesTheWriteARestoreAndDoesNotLeakIntoTheNextTransaction() throws Exception {
    long id = insertAsApp("post", "{\"title\":\"A\"}", "admin");

    db.setAutoCommit(false);
    exec("SELECT set_config('cms.audit_action', 'RESTORE', true)");
    updateAsApp(id, "data = jsonb_set(data, '{title}', '\"B\"')", "jane");
    db.commit();
    db.setAutoCommit(true);
    updateAsApp(id, "data = jsonb_set(data, '{title}', '\"C\"')", "jane");

    List<String> trail = trail(id);
    assertEquals("RESTORE | jane | [\"title\"]", trail.get(1));
    assertEquals("UPDATE | jane | [\"title\"]", trail.get(2));
  }

  @Test
  void aDeleteIsCreditedToWhoeverReSavedTheRowInTheSameTransaction() throws Exception {
    long id = insertAsApp("stat", "{\"label\":\"Doomed\"}", "admin");

    db.setAutoCommit(false);
    updateAsApp(id, "display_order = display_order", "bob"); // the application's re-save
    exec("DELETE FROM content_item WHERE id = " + id);
    db.commit();
    db.setAutoCommit(true);

    List<String> trail = trail(id);
    assertEquals(2, trail.size(), trail.toString()); // the re-save itself records nothing
    assertTrue(trail.get(1).startsWith("DELETE | bob"), trail.toString());
  }

  @Test
  void theTitleStripsMarkupAndFallsBackThroughTheTitleFields() throws Exception {
    assertEquals(
        "Hello World", scalar("SELECT audit_title('{\"title\":\"<p>Hello  <b>World</b></p>\"}')"));
    assertEquals("Jane", scalar("SELECT audit_title('{\"name\":\"Jane\",\"label\":\"x\"}')"));
    assertEquals("", scalar("SELECT audit_title('{}')"));
  }

  @Test
  void archivingIsItsOwnActionAndComingBackToDraftIsAnUpdate() throws Exception {
    long id = insertAsApp("post", "{\"title\":\"Post\"}", "admin");
    updateAsApp(id, "status = 'PUBLISHED'", "admin");
    updateAsApp(id, "status = 'ARCHIVED'", "admin");
    updateAsApp(id, "status = 'DRAFT'", "admin");
    updateAsApp(id, "status = 'PUBLISHED'", "admin");
    updateAsApp(id, "status = 'DRAFT'", "admin");

    List<String> trail = trail(id);
    assertEquals("PUBLISH | admin | [\"status\"]", trail.get(1));
    assertEquals("ARCHIVE | admin | [\"status\"]", trail.get(2));
    assertEquals("UPDATE | admin | [\"status\"]", trail.get(3), "archived back to draft");
    assertEquals("PUBLISH | admin | [\"status\"]", trail.get(4));
    assertEquals("UNPUBLISH | admin | [\"status\"]", trail.get(5), "published back to draft");
  }

  @Test
  void theTableAcceptsTheFourStatusesAndNoOther() throws Exception {
    long id = insertAsApp("post", "{\"title\":\"Post\"}", "admin");
    updateAsApp(id, "status = 'ARCHIVED'", "admin");
    org.junit.jupiter.api.Assertions.assertThrows(
        SQLException.class, () -> updateAsApp(id, "status = 'TRASHED'", "admin"));
  }

  // ---- scheduled publishing -------------------------------------------------------------------

  private long scheduled(String publishAt, String unpublishAt) throws SQLException {
    long id = insertAsApp("post", "{\"title\":\"Post\"}", "editor");
    updateAsApp(
        id,
        "status = 'SCHEDULED', publish_at = "
            + publishAt
            + ", unpublish_at = "
            + (unpublishAt == null ? "NULL" : unpublishAt),
        "editor");
    return id;
  }

  @Test
  void schedulingIsItsOwnActionAndRecordsTheTimes() throws Exception {
    long id = scheduled("now() + interval '1 day'", "now() + interval '2 days'");
    List<String> trail = trail(id);
    assertEquals(2, trail.size());
    assertTrue(trail.get(1).startsWith("SCHEDULE | editor | "), trail.get(1));
    assertTrue(trail.get(1).contains("publishAt") && trail.get(1).contains("unpublishAt"));
    assertTrue(
        scalar(
                "SELECT after_data::text FROM content_audit WHERE item_id = "
                    + id
                    + " ORDER BY id DESC LIMIT 1")
            .contains("\"publishAt\": \""));
  }

  @Test
  void theJobPublishesWhatIsDueAndLeavesTheRestAlone() throws Exception {
    long due = scheduled("now() - interval '1 minute'", null);
    long later = scheduled("now() + interval '1 hour'", null);
    long draft = insertAsApp("post", "{\"title\":\"Draft\"}", "editor");

    assertEquals("1", scalar("SELECT publish_scheduled_content()"));

    assertEquals("PUBLISHED", scalar("SELECT status FROM content_item WHERE id = " + due));
    assertEquals("SCHEDULED", scalar("SELECT status FROM content_item WHERE id = " + later));
    assertEquals("DRAFT", scalar("SELECT status FROM content_item WHERE id = " + draft));
    assertEquals("0", scalar("SELECT publish_scheduled_content()"), "nothing left to do");
    // credited to System, version bumped for the out-of-date-form check
    List<String> trail = trail(due);
    assertEquals("PUBLISH | System | [\"status\"]", trail.get(trail.size() - 1));
    assertEquals("2", scalar("SELECT version FROM content_item WHERE id = " + due));
  }

  @Test
  void theJobCatchesUpOnEverythingOverdue() throws Exception {
    long a = scheduled("now() - interval '3 days'", null);
    long b = scheduled("now() - interval '5 minutes'", null);
    assertTrue(Integer.parseInt(scalar("SELECT publish_scheduled_content()")) >= 2);
    assertEquals("PUBLISHED", scalar("SELECT status FROM content_item WHERE id = " + a));
    assertEquals("PUBLISHED", scalar("SELECT status FROM content_item WHERE id = " + b));
  }

  @Test
  void theJobArchivesALiveItemAtItsUnpublishTimeAndClearsIt() throws Exception {
    long id = insertAsApp("post", "{\"title\":\"Live\"}", "editor");
    updateAsApp(id, "status = 'PUBLISHED', unpublish_at = now() + interval '1 hour'", "editor");
    assertEquals("0", scalar("SELECT publish_scheduled_content()"), "not due yet");
    exec("UPDATE content_item SET unpublish_at = now() - interval '1 minute' WHERE id = " + id);

    assertEquals("1", scalar("SELECT publish_scheduled_content()"));

    assertEquals("ARCHIVED", scalar("SELECT status FROM content_item WHERE id = " + id));
    assertEquals(
        "true", scalar("SELECT (unpublish_at IS NULL)::text FROM content_item WHERE id = " + id));
    List<String> trail = trail(id);
    assertEquals("ARCHIVE | System | [\"status\", \"unpublishAt\"]", trail.get(trail.size() - 1));
  }

  @Test
  void aScheduledItemNeedsATimeAndTheUnpublishTimeMustComeLater() throws Exception {
    long id = insertAsApp("post", "{\"title\":\"Post\"}", "editor");
    org.junit.jupiter.api.Assertions.assertThrows(
        SQLException.class, () -> updateAsApp(id, "status = 'SCHEDULED'", "editor"));
    org.junit.jupiter.api.Assertions.assertThrows(
        SQLException.class,
        () ->
            updateAsApp(
                id,
                "publish_at = now() + interval '2 days', unpublish_at = now() + interval '1 day'",
                "editor"));
  }

  @Test
  void withoutPgCronTheSchemaStillLoadsAndNoJobIsCreated() throws Exception {
    // this test database is not the one pg_cron is configured for, so the job block is skipped
    assertEquals("0", scalar("SELECT count(*) FROM pg_extension WHERE extname = 'pg_cron'"));
  }
}
