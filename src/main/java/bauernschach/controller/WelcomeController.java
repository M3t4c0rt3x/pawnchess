package bauernschach.controller;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class WelcomeController {

  @FXML
  Button startGameButton;

  @FXML
  void initialize() {
    startGameButton.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
        FXMLLoader fxmlLoader =
            new FXMLLoader(getClass().getResource("/GameplayScreen.fxml"));
        try {
          Parent root = fxmlLoader.load();
          Stage gameplayScene = new Stage();
          gameplayScene.setTitle("Bauernschach");
          gameplayScene.setScene(new Scene(root, 800, 600));
          gameplayScene.setResizable(false);
          gameplayScene.show();
          ((Node) (event.getSource())).getScene().getWindow().hide();
        } catch (IOException e) {
          System.exit(0);
        }

      }
    });




  }

}
