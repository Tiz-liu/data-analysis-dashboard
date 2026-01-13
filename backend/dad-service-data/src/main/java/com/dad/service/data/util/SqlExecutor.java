package com.dad.service.data.util;

import com.dad.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.*;

/**
 * SQL Executor
 * Safe SQL execution with validation and pagination
 */
@Slf4j
@Component
public class SqlExecutor {

    /**
     * Test data source connection
     */
    public boolean testConnection(String sourceType, Map<String, Object> config) {
        if ("MYSQL".equalsIgnoreCase(sourceType)) {
            return testMySqlConnection(config);
        }
        throw new BusinessException("Unsupported data source type: " + sourceType);
    }

    /**
     * Execute query and return data
     */
    public Map<String, Object> executeQuery(String sourceType, Map<String, Object> config,
                                             String sql, Integer pageNum, Integer pageSize) {
        if ("MYSQL".equalsIgnoreCase(sourceType)) {
            return executeMySqlQuery(config, sql, pageNum, pageSize);
        }
        throw new BusinessException("Unsupported data source type: " + sourceType);
    }

    /**
     * Test MySQL connection
     */
    private boolean testMySqlConnection(Map<String, Object> config) {
        String url = buildMysqlUrl(config);
        String username = (String) config.get("username");
        String password = (String) config.get("password");

        try (Connection conn = DriverManager.getConnection(url, username, password)) {
            return conn.isValid(5);
        } catch (SQLException e) {
            log.error("MySQL connection test failed", e);
            return false;
        }
    }

    /**
     * Execute MySQL query
     */
    private Map<String, Object> executeMySqlQuery(Map<String, Object> config, String sql,
                                                   Integer pageNum, Integer pageSize) {
        // SQL validation
        validateSql(sql);

        String url = buildMysqlUrl(config);
        String username = (String) config.get("username");
        String password = (String) config.get("password");

        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> rows = new ArrayList<>();
        List<String> columns = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(url, username, password)) {
            // Add pagination
            String paginatedSql = addPagination(sql, pageNum, pageSize);

            try (PreparedStatement stmt = conn.prepareStatement(paginatedSql)) {
                try (ResultSet rs = stmt.executeQuery()) {
                    // Get column names
                    ResultSetMetaData metaData = rs.getMetaData();
                    int columnCount = metaData.getColumnCount();
                    for (int i = 1; i <= columnCount; i++) {
                        columns.add(metaData.getColumnName(i));
                    }

                    // Get rows
                    while (rs.next()) {
                        Map<String, Object> row = new HashMap<>();
                        for (int i = 1; i <= columnCount; i++) {
                            row.put(metaData.getColumnName(i), rs.getObject(i));
                        }
                        rows.add(row);
                    }
                }
            }

            // Get total count
            int total = getTotalCount(conn, sql);

            result.put("success", true);
            result.put("columns", columns);
            result.put("rows", rows);
            result.put("total", total);
            result.put("pageNum", pageNum);
            result.put("pageSize", pageSize);

        } catch (SQLException e) {
            log.error("SQL execution failed", e);
            throw new BusinessException("SQL execution failed: " + e.getMessage());
        }

        return result;
    }

    /**
     * Build MySQL URL from config
     */
    private String buildMysqlUrl(Map<String, Object> config) {
        String host = (String) config.get("host");
        String port = String.valueOf(config.get("port"));
        String database = (String) config.get("database");
        return String.format("jdbc:mysql://%s:%s/%s?useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true",
            host, port, database);
    }

    /**
     * Validate SQL (prevent injection)
     */
    private void validateSql(String sql) {
        String upperSql = sql.toUpperCase().trim();

        // Must start with SELECT
        if (!upperSql.startsWith("SELECT")) {
            throw new BusinessException("Only SELECT queries are allowed");
        }

        // Blacklist keywords
        String[] blackList = {"DROP", "DELETE", "TRUNCATE", "ALTER", "CREATE", "INSERT", "UPDATE", "GRANT", "REVOKE"};
        for (String keyword : blackList) {
            if (upperSql.contains(keyword)) {
                throw new BusinessException("Dangerous keyword detected: " + keyword);
            }
        }

        // Prevent comments (common injection technique)
        if (sql.contains("--") || sql.contains("/*") || sql.contains("*/")) {
            throw new BusinessException("SQL comments are not allowed");
        }
    }

    /**
     * Add pagination to SQL
     */
    private String addPagination(String sql, Integer pageNum, Integer pageSize) {
        int offset = (pageNum - 1) * pageSize;
        // 去掉末尾的分号和空格
        String cleanSql = sql.trim();
        if (cleanSql.endsWith(";")) {
            cleanSql = cleanSql.substring(0, cleanSql.length() - 1).trim();
        }
        return cleanSql + " LIMIT " + offset + ", " + pageSize;
    }

    /**
     * Get total count of query
     */
    private int getTotalCount(Connection conn, String sql) throws SQLException {
        // 去掉末尾的分号和空格
        String cleanSql = sql.trim();
        if (cleanSql.endsWith(";")) {
            cleanSql = cleanSql.substring(0, cleanSql.length() - 1).trim();
        }
        String countSql = "SELECT COUNT(*) FROM (" + cleanSql + ") AS count_table";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(countSql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }
}
