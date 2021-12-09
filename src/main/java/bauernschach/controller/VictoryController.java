package bauernschach.controller;

import java.io.IOException;
import javafx.animation.RotateTransition;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Controller Class for Showing a victory screen and offering a button to start a new game.
 */
public class VictoryController {

  private String player = "";
  private Image victoryImage;
  private Image blackAvatar;
  private Image whiteAvatar;

  @FXML
  Label playerLabel;

  @FXML
  Button newGameButton;

  @FXML
  Circle victorySymbol;

  @FXML
  Circle winnerAvatar;

  VictoryController(String player) {
    this.player = player;
  }

  @FXML
  void initialize() {
    try {
      victoryImage = new Image(getClass().getResource("/images/victory.jpg").toString());
      blackAvatar = new Image(getClass().getResource("/images/blackPawn.png").toString());
      whiteAvatar = new Image(getClass().getResource("/images/whitePawn.png").toString());
    } catch (IllegalArgumentException e) {
      System.err.println("Image not found.");
    }

    if (victoryImage != null) {
      victorySymbol.setFill(new ImagePattern(victoryImage));
      victorySymbol.setRotate(-30);
    }

    if (player.equalsIgnoreCase("BLACK")) {
      playerLabel.setTextFill(Color.BLACK);
      winnerAvatar.setFill(new ImagePattern(blackAvatar));
    } else {
      playerLabel.setTextFill(Color.WHITE);
      winnerAvatar.setStroke(Color.WHITE);
      winnerAvatar.setFill(new ImagePattern(whiteAvatar));
    }
    playerLabel.setText(player.toUpperCase());


    newGameButton.setOnAction(
        new EventHandler<ActionEvent>() {
          @Override
          public void handle(ActionEvent event) {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/GameplayScreen.fxml"));
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

    RotateTransition rt = new RotateTransition(Duration.millis(250), victorySymbol);
    rt.setByAngle(60);
    rt.setDuration(Duration.millis(500));
    rt.setAutoReverse(true);
    rt.setCycleCount(Timeline.INDEFINITE);
    rt.play();
  }
}
