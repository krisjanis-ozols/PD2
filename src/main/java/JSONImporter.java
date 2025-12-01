import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

public class JSONImporter {
    public static void importJSON(String directory) {
        Path dir = Paths.get(directory);

        DB db = new DB();
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
                        System.out.println("Importing: " + p.getFileName());
                        try {
                            String json = Files.readString(p, StandardCharsets.UTF_8);
                            List<Token> tokens = JSONLexer.lex(json);
                            JSONObject root = JSONObject.parseJSON(tokens);
                            importer.importMatch(root);
                        } catch (Exception e) {
                            System.err.println("Failed to import " + p.getFileName() + ": " + e.getMessage());
                            e.printStackTrace();
                        }
                    });
        } catch (IOException e) {
            System.err.println("Error reading directory: " + e.getMessage());
            e.printStackTrace();
        }
        db.close();

        System.out.println("Done.");
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
