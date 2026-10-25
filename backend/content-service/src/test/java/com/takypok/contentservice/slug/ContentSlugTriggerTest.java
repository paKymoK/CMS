package com.takypok.contentservice.slug;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * The slug table is kept by a trigger (init-schema.sql), so it is tested against a real PostgreSQL
 * the same way as the audit trigger: the schema is loaded into a throwaway database and written to
 * as the application would. Needs a reachable server (DB_HOST/DB_PORT/DB_USER/DB_PASSWORD,
 * defaulting to the local dev database); without one every test here is skipped rather than failed.
 */
class ContentSlugTriggerTest {
  private static final String TEST_DB = "content_slug_trigger_test";

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
      assumeTrue(false, "No PostgreSQL reachable for the slug trigger test: " + e.getMessage());
    }
    admin.setAutoCommit(true);
    try (Statement s = admin.createStatement()) {
      s.execute("DROP DATABASE IF EXISTS " + TEST_DB + " WITH (FORCE)");
      s.execute("CREATE DATABASE " + TEST_DB);
    }
    db = DriverManager.getConnection(base + TEST_DB, user, password);
    db.setAutoCommit(true);
    try (InputStream schema =
            ContentSlugTriggerTest.class
                .getClassLoader()
                .getResourceAsStream("db/changelog/init/init-schema.sql");
        Statement s = db.createStatement()) {
      s.execute(new String(schema.readAllBytes(), StandardCharsets.UTF_8));
      s.execute(
          "INSERT INTO site (code, subdomain) VALUES ('en', 'localhost'), ('vi', 'vi.localhost')");
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

  @BeforeEach
  void clean() throws Exception {
    exec("DELETE FROM content_item");
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

  private long insert(long siteId, String type, String data, String status) throws SQLException {
    try (Statement s = db.createStatement();
        ResultSet rs =
            s.executeQuery(
                "INSERT INTO content_item (site_id, content_type, data, status) VALUES ("
                    + siteId
                    + ", '"
                    + type
                    + "', '"
                    + data
                    + "', '"
                    + status
                    + "') RETURNING id")) {
      rs.next();
      return rs.getLong(1);
    }
  }

  private long insertPost(String slug, String status) throws SQLException {
    return insert(1, "post", "{\"slug\":\"" + slug + "\"}", status);
  }

  private void setData(long id, String data) throws SQLException {
    exec("UPDATE content_item SET data = '" + data + "' WHERE id = " + id);
  }

  private void setSlug(long id, String slug) throws SQLException {
    setData(id, "{\"slug\":\"" + slug + "\"}");
  }

  /** The item's slugs as "scope/slug" (or just "slug"), primary first and marked with a star. */
  private List<String> slugs(long id) throws SQLException {
    List<String> rows = new ArrayList<>();
    try (Statement s = db.createStatement();
        ResultSet rs =
            s.executeQuery(
                "SELECT CASE WHEN scope = '' THEN '' ELSE scope || '/' END || slug"
                    + " || CASE WHEN is_primary THEN '*' ELSE '' END"
                    + " FROM content_slug WHERE item_id = "
                    + id
                    + " ORDER BY is_primary DESC, id")) {
      while (rs.next()) rows.add(rs.getString(1));
    }
    return rows;
  }

  // ---- the behaviour --------------------------------------------------------------------------

  @Test
  void aNewItemsSlugBecomesItsPrimary() throws Exception {
    long id = insertPost("hello", "DRAFT");

    assertEquals(List.of("hello*"), slugs(id));
  }

  @Test
  void renamingAPublishedItemKeepsTheOldSlugAsAnAlias() throws Exception {
    long id = insertPost("hello-world", "PUBLISHED");

    setSlug(id, "hello-cms");
    setSlug(id, "hello-cms-guide");

    assertEquals(List.of("hello-cms-guide*", "hello-world", "hello-cms"), slugs(id));
  }

  @Test
  void renamingADraftThatWasNeverLiveLeavesNoAlias() throws Exception {
    long id = insertPost("helo", "DRAFT");

    setSlug(id, "hello");

    assertEquals(List.of("hello*"), slugs(id));
  }

  @Test
  void aDraftThatWasPublishedBeforeKeepsItsOldSlug() throws Exception {
    long id = insertPost("first", "PUBLISHED");
    exec(
        "INSERT INTO content_audit (site_id, item_id, content_type, action) VALUES (1, "
            + id
            + ", 'post', 'PUBLISH')");
    exec("UPDATE content_item SET status = 'DRAFT' WHERE id = " + id);

    setSlug(id, "second");

    assertEquals(List.of("second*", "first"), slugs(id));
  }

  @Test
  void changingBackToAnOldSlugMakesItThePrimaryAgain() throws Exception {
    long id = insertPost("hello-world", "PUBLISHED");
    setSlug(id, "hello-cms");

    setSlug(id, "hello-world");

    assertEquals(List.of("hello-world*", "hello-cms"), slugs(id));
  }

  @Test
  void aSlugAnotherItemUsesAsPrimaryOrAliasIsRejected() throws Exception {
    long first = insertPost("alpha", "PUBLISHED");
    setSlug(first, "beta"); // alpha is now an alias of the first item
    long second = insertPost("gamma", "PUBLISHED");

    assertThrows(SQLException.class, () -> setSlug(second, "alpha"));
    assertThrows(SQLException.class, () -> setSlug(second, "beta"));
    assertThrows(SQLException.class, () -> insertPost("alpha", "DRAFT"));
    assertEquals(List.of("gamma*"), slugs(second));
  }

  @Test
  void theSameSlugIsFineForOtherTypesAndOtherSites() throws Exception {
    long post = insertPost("overview", "PUBLISHED");
    long caseStudy = insert(1, "case_study", "{\"slug\":\"overview\"}", "PUBLISHED");
    long otherSite = insert(2, "post", "{\"slug\":\"overview\"}", "PUBLISHED");

    assertEquals(List.of("overview*"), slugs(post));
    assertEquals(List.of("overview*"), slugs(caseStudy));
    assertEquals(List.of("overview*"), slugs(otherSite));
  }

  @Test
  void aServicePageSlugIsUniquePerCategoryAndMovingCategoryKeepsTheOldAddress() throws Exception {
    long web = insert(1, "service_page", "{\"slug\":\"dev\",\"category\":\"web\"}", "PUBLISHED");
    long mobile =
        insert(1, "service_page", "{\"slug\":\"dev\",\"category\":\"mobile\"}", "PUBLISHED");

    assertEquals(List.of("web/dev*"), slugs(web));
    assertEquals(List.of("mobile/dev*"), slugs(mobile));
    assertThrows(
        SQLException.class,
        () -> insert(1, "service_page", "{\"slug\":\"dev\",\"category\":\"web\"}", "DRAFT"));

    setData(web, "{\"slug\":\"dev\",\"category\":\"cloud\"}");

    assertEquals(List.of("cloud/dev*", "web/dev"), slugs(web));
  }

  @Test
  void deletingTheItemDeletesItsSlugs() throws Exception {
    long id = insertPost("hello-world", "PUBLISHED");
    setSlug(id, "hello-cms");

    exec("DELETE FROM content_item WHERE id = " + id);

    assertEquals(List.of(), slugs(id));
    // the freed addresses can be used again
    assertEquals(List.of("hello-world*"), slugs(insertPost("hello-world", "DRAFT")));
  }

  @Test
  void typesWithoutADetailPageAreNotTracked() throws Exception {
    long id = insert(1, "service_category", "{\"slug\":\"web\"}", "PUBLISHED");

    assertEquals(List.of(), slugs(id));
  }

  @Test
  void savingWithoutChangingTheSlugWritesNothing() throws Exception {
    long id = insertPost("steady", "PUBLISHED");

    setData(id, "{\"slug\":\"steady\",\"title\":\"Changed\"}");

    assertEquals(List.of("steady*"), slugs(id));
  }
}
