package com.bupt.ta.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class JsonStorage {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final String DATA_DIR = "data";

    static {
        Path dir = Paths.get(DATA_DIR);
        if (!Files.exists(dir)) {
            try {
                Files.createDirectories(dir);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static <T> void save(String filename, List<T> items) {
        Path path = Paths.get(DATA_DIR, filename);
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(path.toFile()), StandardCharsets.UTF_8)) {
            gson.toJson(items, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static <T> List<T> load(String filename, Type type) {
        Path path = Paths.get(DATA_DIR, filename);
        if (!Files.exists(path)) {
            return new ArrayList<>();
        }
        try (Reader reader = new InputStreamReader(new FileInputStream(path.toFile()), StandardCharsets.UTF_8)) {
            List<T> result = gson.fromJson(reader, type);
            return result != null ? result : new ArrayList<>();
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static <T> List<T> loadList(String filename, Class<T> clazz) {
        Type type = TypeToken.getParameterized(List.class, clazz).getType();
        return load(filename, type);
    }
}
