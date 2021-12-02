package bauernschach.view;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) throws IOException {
    FXMLLoader fxmlLoader =
        new FXMLLoader(getClass().getResource("/WelcomeScreen.fxml"));
      Parent root = fxmlLoader.load();
      primaryStage.setTitle("Bauernschach");
      primaryStage.setScene(new Scene(root, 800, 600));
      primaryStage.setResizable(false);
      primaryStage.show();
  }
}
