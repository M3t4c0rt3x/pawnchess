package bauernschach.controller;

import bauernschach.model.Bauernschach;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class VictoryController {
  private String player = "";


  @FXML
  Label playerLabel;

  @FXML
  Button newGameButton;

  @FXML
  void initialize() {
    player = GameplayController.player;
    if (player.toUpperCase().equals("BLACK")) {
      playerLabel.setTextFill(Color.BLACK);
    } else {
      playerLabel.setTextFill(Color.WHITE);
    }
    playerLabel.setText(player.toUpperCase());

    newGameButton.setOnAction(new EventHandler<ActionEvent>() {
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
          e.getCause().getCause().printStackTrace();
        }
      }
    });
  }
}
