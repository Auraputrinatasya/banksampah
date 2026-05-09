package com.banksampah.util;

import com.banksampah.model.User;

public class SessionManager {
    private static User currentUser;

    public static void setCurrentUser(User user) { currentUser = user; }
    public static User getCurrentUser() { return currentUser; }
    public static boolean isAdmin() { return currentUser != null && "admin".equals(currentUser.getRole()); }
    public static void logout() { currentUser = null; }
}
