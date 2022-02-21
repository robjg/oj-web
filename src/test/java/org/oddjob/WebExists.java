package org.oddjob;

public class WebExists {

    public static boolean check() {
        return WebExists.class.getClassLoader().getResource("dist/index.html") != null;
    }
}
