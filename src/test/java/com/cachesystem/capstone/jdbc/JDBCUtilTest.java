package com.cachesystem.capstone.jdbc;

import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class JDBCUtilTest {

    private static Connection connection;
    private JDBCUtil jdbcUtil;
    private static final Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
    private static final String DB_URL = dotenv.get("DB_URL");
    private static final String DB_USER = dotenv.get("DB_USER");
    private static final String DB_PASSWORD = dotenv.get("DB_PASSWORD");

    @BeforeAll
    public static void setUpBeforeClass() throws SQLException {
        connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

        try (Statement stmt = connection.createStatement()) {
            // Disable FK checks temporarily
            stmt.execute("SET FOREIGN_KEY_CHECKS = 0");

            stmt.execute("DELETE FROM CacheManager_Cache");
            stmt.execute("DELETE FROM Exception");
            stmt.execute("DELETE FROM Cache");
            stmt.execute("DELETE FROM LruPolicy");
            stmt.execute("DELETE FROM LfuPolicy");
            stmt.execute("DELETE FROM TtlPolicy");
            stmt.execute("DELETE FROM EvictionPolicy");
            stmt.execute("DELETE FROM CacheManager");

            // Re-enable FK checks
            stmt.execute("SET FOREIGN_KEY_CHECKS = 1");

            // Reinsert minimal needed data
            stmt.execute("INSERT INTO EvictionPolicy (id, name, description) VALUES " +
                    "(1, 'LRU', 'Least Recently Used'), (2, 'LFU', 'Least Frequently Used')");

            stmt.execute("INSERT INTO Cache (id, name, value, eviction_policy_id) VALUES " +
                    "(1, 'CacheItem1', 'value1', 1), " +
                    "(2, 'CacheItem2', 'value2', 2)");
        }
    }



    @BeforeEach
    public void setUp() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        }

        jdbcUtil = new JDBCUtil(connection);

        try (Statement stmt = connection.createStatement()) {
            stmt.execute("SET FOREIGN_KEY_CHECKS = 0");
            stmt.execute("DELETE FROM Cache");
            stmt.execute("SET FOREIGN_KEY_CHECKS = 1");

            stmt.execute("INSERT INTO Cache (id, name, value, eviction_policy_id) VALUES " +
                    "(1, 'CacheItem1', 'value1', 1), " +
                    "(2, 'CacheItem2', 'value2', 2)");
        }
    }


//    @AfterEach
//    public void cleanUp() throws SQLException {
//        try (Statement stmt = connection.createStatement()) {
//            stmt.execute("DELETE FROM Cache;");
//            stmt.execute("INSERT INTO Cache (id, name, value, eviction_policy_id) VALUES " +
//                    "(1, 'CacheItem1', 'value1', 1), " +
//                    "(2, 'CacheItem2', 'value2', 2);");
//        }
//    }

    @AfterAll
    public static void tearDownAfterClass() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }


    private String getName(ResultSet rs) {
        try {
            return rs.getString("name");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to read name", e);
        }
    }

    @Test
    void testExecuteInsert() {
        jdbcUtil.execute("INSERT INTO Cache (id, name, value, eviction_policy_id) VALUES (?, ?, ?, ?)",
                3, "CacheItem3", "value3", 1);

        String name = jdbcUtil.findOne(
                "SELECT name FROM Cache WHERE id = ?",
                this::getName,
                3
        );

        assertEquals("CacheItem3", name);
    }

    @Test
    void testFindEvictionPolicyForCache() {
        String policyName = jdbcUtil.findOne(
                "SELECT e.name FROM Cache c JOIN EvictionPolicy e ON c.eviction_policy_id = e.id WHERE c.id = ?",
                this::getName,
                2
        );

        assertEquals("LFU", policyName);
    }

    @Test
    void testFindMany() {
        List<String> names = jdbcUtil.findMany(
                "SELECT name FROM Cache",
                this::getName
        );

        assertEquals(2, names.size());
        assertTrue(names.contains("CacheItem1"));
        assertTrue(names.contains("CacheItem2"));
    }

    @Test
    void testExecuteUpdate() {
        jdbcUtil.execute("INSERT INTO Cache (id, name, value, eviction_policy_id) VALUES (?, ?, ?, ?)",
                4, "CacheItem4", "value4", 1);

        jdbcUtil.execute("UPDATE Cache SET name = ? WHERE id = ?", "UpdatedCacheItem4", 4);

        String updatedName = jdbcUtil.findOne(
                "SELECT name FROM Cache WHERE id = ?",
                this::getName,
                4
        );

        assertEquals("UpdatedCacheItem4", updatedName);
    }

    @Test
    void testExecuteWithConsumer() {
        jdbcUtil.execute("INSERT INTO Cache (id, name, value, eviction_policy_id) VALUES (?, ?, ?, ?)", stmt -> {
            try {
                stmt.setInt(1, 5);
                stmt.setString(2, "CacheItem5");
                stmt.setString(3, "value5");
                stmt.setInt(4, 1);
            } catch (SQLException e) {
                throw new RuntimeException("Error setting parameters", e);
            }
        });

        String name = jdbcUtil.findOne(
                "SELECT name FROM Cache WHERE id = ?",
                this::getName,
                5
        );

        assertEquals("CacheItem5", name);
    }

    @Test
    void testFindOneNotFound() {
        String name = jdbcUtil.findOne(
                "SELECT name FROM Cache WHERE id = ?",
                this::getName,
                999
        );
        assertNull(name);
    }

    @Test
    void testFindOneMultipleRowsThrows() {
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                jdbcUtil.findOne("SELECT name FROM Cache", this::getName)
        );
        assertTrue(exception.getMessage().contains("Expected one result"));
    }
}
