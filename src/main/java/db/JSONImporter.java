package db;

import json_parser.JSONLexer;
import json_parser.JSONObject;
import json_parser.Token;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class JSONImporter {
    public static void importJSON(String directory, DB db) {
        importJSON(directory, db, System.out::println);
    }
    public static void importJSON(String directory, DB db, Consumer<String> logger) {
        Path dir = Paths.get(directory);

        JSONToDB importer = new JSONToDB(db);

        try (Stream<Path> paths = Files.list(dir)) {
            paths
                    .filter(Files::isRegularFile)
                    .filter(p -> p.getFileName().toString().endsWith(".json"))
                    .sorted((a, b) -> {
                        int na = extractNumber(a.getFileName().toString());
                        int nb = extractNumber(b.getFileName().toString());
                        return Integer.compare(na, nb);
                    })
                    .forEach(p -> {
                        logger.accept("Importē: " + p.getFileName());
                        try {
                            String json = Files.readString(p, StandardCharsets.UTF_8);
                            List<Token> tokens = JSONLexer.lex(json);
                            JSONObject root = JSONObject.parseJSON(tokens);
                            importer.importMatch(root);
                        } catch (Exception e) {
                            logger.accept("Neizdevās Importēt " + p.getFileName() + ": " + "\n" + e.getMessage());
                            e.printStackTrace();
                        }
                    });
        } catch (IOException e) {
            logger.accept("Error reading directory: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static int extractNumber(String filename) {
        StringBuilder number = new StringBuilder();

        for (int i = 0; i < filename.length(); i++) {
            char c = filename.charAt(i);
            if (Character.isDigit(c)) {
                number.append(c);
            }
        }

        if (number.isEmpty()) {
            return -1;
        }

        return Integer.parseInt(number.toString());
    }
}
