package com.campus.library.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class Database {
    private static HikariDataSource dataSource;

    static {
        try {
            Properties props = new Properties();
            try (InputStream in = Database.class.getClassLoader().getResourceAsStream("application.properties")) {
                if (in != null) props.load(in);
            }
            HikariConfig cfg = new HikariConfig();
            cfg.setJdbcUrl(props.getProperty("db.url", "jdbc:mysql://localhost:3306/campus_library?useSSL=false&serverTimezone=UTC"));
            cfg.setUsername(props.getProperty("db.username", "root"));
            cfg.setPassword(props.getProperty("db.password", ""));
            cfg.setMaximumPoolSize(Integer.parseInt(props.getProperty("db.pool.max", "10")));
            cfg.setMinimumIdle(Integer.parseInt(props.getProperty("db.pool.min", "2")));
            cfg.setPoolName("LibraryHikariCP");
            dataSource = new HikariDataSource(cfg);
        } catch (Exception e) {
            throw new RuntimeException("初始化数据库连接池失败: " + e.getMessage(), e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public static void close() {
        if (dataSource != null) dataSource.close();
    }
}
