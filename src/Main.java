import controllers.IndexController;
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
        FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/index.fxml"), bundle);

        Parent root = loader.load();
        primaryStage.setTitle(bundle.getString("title"));
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("img/deezer-icon.jpg")));
        primaryStage.setScene(new Scene(root));
        IndexController controller = loader.getController();

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
