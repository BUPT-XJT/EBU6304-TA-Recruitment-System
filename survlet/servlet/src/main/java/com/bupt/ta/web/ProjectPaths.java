package com.bupt.ta.web;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Resolves the Maven module root (directory that contains {@code data/users.txt}).
 * {@link com.bupt.ta.web.WebServerMain} calls {@link #syncModuleRootWithWebapp(Path)} so data
 * stays aligned with the same webapp used for JSP (fixes login when CWD or JAR layout differs).
 * Override with {@code -Dta.moduleRoot=E:\path\to\servlet}.
 */
public final class ProjectPaths {

    private ProjectPaths() {}

    /**
     * Walks up from the resolved webapp directory until {@code data/users.txt} is found,
     * then sets {@code ta.moduleRoot} if the user did not set it on the command line.
     */
    public static void syncModuleRootWithWebapp(Path webappRoot) {
        Path normalized = webappRoot.toAbsolutePath().normalize();
        Path cur = normalized;
        for (int depth = 0; depth < 12; depth++) {
            if (Files.isRegularFile(cur.resolve("data/users.txt"))) {
                setModuleRootIfUnset(cur);
                return;
            }
            if (cur.getParent() == null) {
                break;
            }
            cur = cur.getParent();
        }
        Path fallback = normalized.getParent() != null && normalized.getParent().getParent() != null
                ? normalized.getParent().getParent()
                : normalized;
        setModuleRootIfUnset(fallback);
    }

    private static void setModuleRootIfUnset(Path root) {
        String p = System.getProperty("ta.moduleRoot");
        if (p == null || p.isBlank()) {
            System.setProperty("ta.moduleRoot", root.toString());
        }
    }

    public static Path moduleRoot() {
        String override = System.getProperty("ta.moduleRoot");
        if (override != null && !override.isBlank()) {
            return Paths.get(override).toAbsolutePath().normalize();
        }

        Path cwd = Paths.get("").toAbsolutePath();
        if (Files.isRegularFile(cwd.resolve("data/users.txt"))) {
            return cwd.normalize();
        }

        try {
            var source = ProjectPaths.class.getProtectionDomain().getCodeSource();
            if (source != null && source.getLocation() != null) {
                URI uri = source.getLocation().toURI();
                Path codePath = Paths.get(uri);
                if (Files.isDirectory(codePath)) {
                    Path mr = codePath.getParent().getParent();
                    if (Files.isRegularFile(mr.resolve("data/users.txt"))) {
                        return mr.normalize();
                    }
                } else if (Files.isRegularFile(codePath)) {
                    String fn = codePath.getFileName().toString().toLowerCase();
                    if (fn.endsWith(".jar")) {
                        Path targetDir = codePath.getParent();
                        if (targetDir != null) {
                            Path moduleFromTarget = targetDir.getParent();
                            if (moduleFromTarget != null
                                    && Files.isRegularFile(moduleFromTarget.resolve("data/users.txt"))) {
                                return moduleFromTarget.normalize();
                            }
                            if (Files.isRegularFile(targetDir.resolve("data/users.txt"))) {
                                return targetDir.normalize();
                            }
                        }
                    }
                }
            }
        } catch (Exception ignored) {
        }

        return cwd.normalize();
    }

    /** Path to a file under {@code data/}, e.g. {@code users.txt}. */
    public static String dataFile(String nameInDataDir) {
        return moduleRoot().resolve("data").resolve(nameInDataDir).toString();
    }
}
