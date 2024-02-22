package com.API;

import java.util.Map;
import java.util.HashMap;
import javax.servlet.http.HttpSession;

public class UserSessionManager {
    private static Map<String, HttpSession> activeSessions = new HashMap<>();
    
    public static HttpSession getActiveSession(String userId) {
        return activeSessions.get(userId);
    }
    
    public static void addActiveSession(String userId, HttpSession session) {
        activeSessions.put(userId, session);
    }
    
    public static void invalidateSession(String userId) {
        HttpSession session = activeSessions.get(userId);
        if (session != null) {
            session.invalidate();
            activeSessions.remove(userId);
        }
    }
}
