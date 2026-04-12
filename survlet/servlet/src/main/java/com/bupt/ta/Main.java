package com.bupt.ta;

import com.bupt.ta.web.WebServerMain;

/** Starts embedded Jetty (Servlet API + static files under {@code src/webapp}). */
public class Main {
    public static void main(String[] args) {
        try {
            WebServerMain.main(args);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
