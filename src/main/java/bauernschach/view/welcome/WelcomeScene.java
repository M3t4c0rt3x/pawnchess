package bauernschach.view.welcome;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class WelcomeScene extends Scene {

  private static final int DEFAULT_WINDOW_WIDTH = 300;
  private static final int DEFAULT_WINDOW_HEIGHT = (int) (DEFAULT_WINDOW_WIDTH / 1.618);

  public WelcomeScene() {
    super(createContent(), DEFAULT_WINDOW_WIDTH, DEFAULT_WINDOW_HEIGHT);
  }

  private static Parent createContent() {
    Node banner = new Text("Welcome to Bauernschach!");

    Parent pane = new StackPane(banner);
    return pane;
  }

  public void start(Stage primaryStage) throws IOException {
    FXMLLoader fxmlLoader =
        new FXMLLoader(getClass().getResource("/WelcomeWindow.fxml"));
    Parent root = fxmlLoader.load();
    primaryStage.setTitle("Welcome to the pawn chess game");
    primaryStage.setScene(new Scene(root, 300, 275));
    primaryStage.show();
  }
}


