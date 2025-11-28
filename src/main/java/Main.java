import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        String json ="";
        try {
            Path path = Path.of("futbols0.json");   // adjust path if needed
            json = Files.readString(path, StandardCharsets.UTF_8);
        }
        catch(Exception e){

        }

        //String json = "{ \"team\": { \"name\": \"Dragons\", \"founded\": 1998 }, \"active\": false }";

        List<Token> tokens = JSONLexer.lex(json);
        JSONObject root = JSONObject.parseJSON(tokens);
        //Map<String, JSONObject> testMap= new HashMap<>();

//        JSONObject test2 = new JSONObject("test2","test2");
//        JSONObject root = new JSONObject("test1","test1");
//        root.addChild("dingus",test2);
//        root.addChild("bingus",test2);

        //printTokens(tokens);

//        printObject(root, 0);
//        JSONObject[] test = root.getObject("Spele").getArray("Komanda");
//        JSONObject[] test2=test[0].getObject("Speletaji").getArray("Speletajs");
//        int test3 = test2[0].getInt("Nr");
//        System.out.println(test3);
//        DB SQLite = new DB();
//        try {
//            int id = SQLite.playerId(SQLite.teamId("test"),1,"Te","St","U");
//            System.out.println(id);
//        }
//        catch(SQLException e){
//            e.printStackTrace();
//        }

        try {
            DB db = new DB();
            JSONToDB importer = new JSONToDB(db);

            importer.importMatch(root);

            System.out.println("Import done.");
        } catch (Exception e) {
            e.printStackTrace();
        }
        }


    private static void printObject(JSONObject obj, int indent) {
        String pad = "  ".repeat(indent);
        System.out.println(pad + "type=" + obj.getType() + ", value=" + obj.getValue());

        if (obj.getChildren() != null) {
            for (Map.Entry<String, JSONObject> entry : obj.getChildren().entrySet()) {
                System.out.println(pad + "key = " + entry.getKey());
                printObject(entry.getValue(), indent + 1);
            }
        }
    }
    private static void printTokens(List<Token> tokens){
        for(Token token: tokens){
            System.out.println("type = " + token.getType() + ", value = " + token.getValue());
        }
    }
}
