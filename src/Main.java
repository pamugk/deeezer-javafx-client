import controllers.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.ResourceBundle;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception{
        ResourceBundle bundle = ResourceBundle.getBundle("localisation/localisation");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/main.fxml"), bundle);

        Parent root = loader.load();
        primaryStage.setTitle(bundle.getString("title"));
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("img/deezer-icon")));
        primaryStage.setScene(new Scene(root));
        MainController controller = loader.getController();

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
