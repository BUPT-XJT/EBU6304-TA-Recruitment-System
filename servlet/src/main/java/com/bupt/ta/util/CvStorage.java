package com.bupt.ta.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Optional;

public final class CvStorage {
    private static final String DATA_DIR = "data";
    private static final String CV_DIR = "cvs";
    public static final long MAX_BYTES = 10L * 1024 * 1024;

    private CvStorage() {}

    public static Optional<String> storeCv(String userId, File sourceFile, String previousRelativePath) {
        if (userId == null || userId.isBlank() || sourceFile == null || !sourceFile.isFile()) {
            return Optional.empty();
        }
        try {
            long size = Files.size(sourceFile.toPath());
            if (size == 0 || size > MAX_BYTES) {
                return Optional.empty();
            }
        } catch (IOException e) {
            return Optional.empty();
        }
        String origName = sourceFile.getName();
        if (!hasAllowedExtension(origName)) {
            return Optional.empty();
        }
        String safe = sanitizeFileName(origName);
        String storedFileName = userId + "_" + safe;
        Path dir = Paths.get(DATA_DIR, CV_DIR);
        try {
            Files.createDirectories(dir);
        } catch (IOException e) {
            return Optional.empty();
        }
        Path target = dir.resolve(storedFileName);
        deleteStoredFile(previousRelativePath);
        try {
            Files.copy(sourceFile.toPath(), target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            return Optional.empty();
        }
        return Optional.of(CV_DIR + "/" + storedFileName);
    }

    public static void deleteStoredFile(String relativeUnderData) {
        Path p = resolveStoredPath(relativeUnderData);
        if (p == null) {
            return;
        }
        try {
            Files.deleteIfExists(p);
        } catch (IOException ignored) {
        }
    }

    public static boolean storedFileExists(String relativeUnderData) {
        Path p = resolveStoredPath(relativeUnderData);
        return p != null && Files.isRegularFile(p);
    }

    public static Path resolveStoredPath(String relativeUnderData) {
        if (relativeUnderData == null || relativeUnderData.isBlank()) {
            return null;
        }
        Path base = Paths.get(DATA_DIR, CV_DIR).toAbsolutePath().normalize();
        Path abs = Paths.get(DATA_DIR).resolve(relativeUnderData).normalize().toAbsolutePath().normalize();
        if (!abs.startsWith(base)) {
            return null;
        }
        return abs;
    }

    private static boolean hasAllowedExtension(String name) {
        String lower = name.toLowerCase(Locale.ROOT);
        return lower.endsWith(".pdf") || lower.endsWith(".doc") || lower.endsWith(".docx");
    }

    private static String sanitizeFileName(String name) {
        String base = new File(name).getName();
        base = base.replaceAll("[^a-zA-Z0-9._-]", "_");
        if (base.length() > 120) {
            base = base.substring(base.length() - 120);
        }
        return base.isEmpty() ? "cv.pdf" : base;
    }
}
