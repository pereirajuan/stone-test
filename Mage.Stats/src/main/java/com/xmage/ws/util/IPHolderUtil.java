package com.xmage.ws.util;

/**
 * Stores ip addresses to allow access from.
 * Stores user-agents to allow access for.
 *
 * @author noxx
 */
public class IPHolderUtil {

    private static final ThreadLocal<String> ipThreadLocal = new ThreadLocal<String>();
    private static final ThreadLocal<String> userAgentThreadLocal = new ThreadLocal<String>();

    private IPHolderUtil() {}

    public static void rememberIP(String ip) {
        ipThreadLocal.set(ip);
    }
    
    public static String getRememberedIP() {
        return ipThreadLocal.get();
    }
    
    public static void rememberUserAgent(String userAgent) {
        userAgentThreadLocal.set(userAgent);
    }
    
    public static String getRememberedUserAgent() {
        return userAgentThreadLocal.get();
    }
}

