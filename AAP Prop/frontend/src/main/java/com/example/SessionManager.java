package com.example;

public class SessionManager {

    private static String loggedInUser;
    private static String loggedInRole;

    public static void setLoggedInUser(String username) {
        loggedInUser = username;
    }

    public static String getLoggedInUser() {
        return loggedInUser;
    }

    public static void setLoggedInRole(String role) {
        loggedInRole = role;
    }

    public static String getLoggedInRole() {
        return loggedInRole;
    }

    public static void clear() {
        loggedInUser = null;
        loggedInRole = null;
    }
}