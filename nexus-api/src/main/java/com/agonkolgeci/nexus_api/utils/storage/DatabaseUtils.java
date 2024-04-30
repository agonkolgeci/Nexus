package com.agonkolgeci.nexus_api.utils.storage;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class DatabaseUtils {

    @NotNull
    public static List<String> retrieveStatements(@NotNull final InputStream inputStream) throws IOException {
        @NotNull final List<String> statements = new ArrayList<>();

        @NotNull final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        @NotNull final StringBuilder sqlBuilder = new StringBuilder();

        @NotNull String line;
        while ((line = reader.readLine()) != null) {
            if(line.startsWith("--") || line.startsWith("#")) continue;

            sqlBuilder.append(line).append("\n");

            if(line.trim().endsWith(";")) {
                statements.add(sqlBuilder.toString());
                sqlBuilder.setLength(0);
            }
        }

        reader.close();

        return statements;
    }
}
