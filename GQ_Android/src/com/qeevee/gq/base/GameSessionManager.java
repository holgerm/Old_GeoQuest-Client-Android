package com.qeevee.gq.base;

public class GameSessionManager {
    
    private static String sessionID = null;

    public static String getSessionID() {
	return sessionID;
    }

    public static void setSessionID(String gameName) {
	GameSessionManager.sessionID = gameName;
    }

}
