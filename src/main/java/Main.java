import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/main.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setTitle("Futbola statistika");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }



//    private static void printObject(JSONObject obj, int indent) {
//        String pad = "  ".repeat(indent);
//        System.out.println(pad + "type=" + obj.getType() + ", value=" + obj.getValue());
//
//        if (obj.getChildren() != null) {
//            for (Map.Entry<String, JSONObject> entry : obj.getChildren().entrySet()) {
//                System.out.println(pad + "key = " + entry.getKey());
//                printObject(entry.getValue(), indent + 1);
//            }
//        }
//    }
//    private static void printTokens(List<Token> tokens){
//        for(Token token: tokens){
//            System.out.println("type = " + token.getType() + ", value = " + token.getValue());
//        }
//    }
}
