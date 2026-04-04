package com.campus.library;

import java.io.InputStream;
import java.util.Properties;

public class Config {
    private static final Properties props = new Properties();

    static {
        try (InputStream in = Config.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (in != null) props.load(in);
        } catch (Exception ignored) {}
    }

    public static int loanDays() { return Integer.parseInt(props.getProperty("loan.days", "14")); }
    public static int renewDays() { return Integer.parseInt(props.getProperty("renew.days", "7")); }
    public static int maxBorrowForRole(String role) {
        if ("staff".equalsIgnoreCase(role)) return Integer.parseInt(props.getProperty("max.borrow.staff", "10"));
        return Integer.parseInt(props.getProperty("max.borrow.student", "5"));
    }
}
