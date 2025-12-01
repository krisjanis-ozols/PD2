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

        JSONImporter.importJSON("SUBTEST");

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
