package bauernschach.controller;

import bauernschach.model.Bauernschach;
import bauernschach.model.GameState;
import bauernschach.model.board.ChessPiece;
import bauernschach.model.board.ChessPiece.Color;
import bauernschach.model.board.Coordinate;
import bauernschach.model.board.Move;
import bauernschach.model.observable.Observer;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.RotateTransition;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;

/** Controller for connecting the GameplayScreen with the model Bauernschach / Gamestate. */
public class GameplayController implements Observer {

  private Bauernschach game = new Bauernschach();
  private Circle selectedCircle;
  private String player;

  @FXML private GridPane chessBoardGridPane;

  @FXML private Label currentPlayerLabel;

  @FXML private Button passButton;

  @FXML
  void initialize() {
    game.subscribe(this);
    player = game.getGameState().getCurrentRound().toString();
    currentPlayerLabel.setText("current player: " + player.toUpperCase());

    // Rotation of the board, so that the currentPLayer always has the lower rows
    RotateTransition rt = new RotateTransition(Duration.millis(1000), chessBoardGridPane);
    rt.setByAngle(180);
    rt.play();

    displayCurrentBoard();
  }

  @Override
  public void updateState(GameState state) {
    player = game.getGameState().getCurrentRound().toString();
    final GameState gameState = game.getGameState();
    final GameState.GameStatus gameStatus = gameState.getStatus();

    // if either white or black has won
    if ((gameStatus == GameState.GameStatus.WHITE_WON)
        || (gameStatus == GameState.GameStatus.BLACK_WON)) {

      FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/VictoryScreen.fxml"));
      fxmlLoader.setController(new VictoryController(player));
      try {
        Parent root = fxmlLoader.load();
        Stage gameplayScene = new Stage();
        gameplayScene.setTitle("Bauernschach");
        gameplayScene.setScene(new Scene(root, 800, 600));
        gameplayScene.setResizable(false);
        gameplayScene.show();
        ((Stage) (chessBoardGridPane.getScene().getWindow())).close();
        game.unsubscribe(this);
      } catch (IOException e) {
        e.getCause().getCause().printStackTrace();
      }
      // or the game is ongoing
    } else {
      currentPlayerLabel.setText("current player: " + player.toUpperCase());
      displayCurrentBoard();
    }
  }

  /**
   * Method, displaying all pieces at the board and calculating/displaying in sub-methods possible
   * moves.
   */
  private void displayCurrentBoard() {
    final GameState gameState = game.getGameState();
    chessBoardGridPane.getChildren().clear();

    passButton.setOnMouseClicked(
        e -> {
          game.pass();

          // Rotation of the board, so that the currentPLayer always has the lower rows
          RotateTransition rt = new RotateTransition(Duration.millis(250), chessBoardGridPane);
          rt.setByAngle(180);
          rt.play();
        });

    for (int actualRow = 0; actualRow < 8; actualRow++) {
      for (int actualColumn = 0; actualColumn < 8; actualColumn++) {
        ChessPiece currentPiece =
            gameState.getChessBoard().getPieceAt(Coordinate.of(actualRow, actualColumn));

        /* add pieces on their corresponding position to the board */
        if (currentPiece.getColor() != null) {
          int id = currentPiece.getId();
          if (currentPiece.getColor() == Color.BLACK) {
            Circle currentCircle =
                createNewClickableCircle(Paint.valueOf("BLACK"), id, actualColumn, actualRow);
            chessBoardGridPane.add(currentCircle, actualColumn, actualRow);
            GridPane.setHalignment(currentCircle, HPos.CENTER);
          } else {
            Circle currentCircle =
                createNewClickableCircle(Paint.valueOf("WHITE"), id, actualColumn, actualRow);
            chessBoardGridPane.add(currentCircle, actualColumn, actualRow);
            GridPane.setHalignment(currentCircle, HPos.CENTER);
          }
        }
      }
    }
  }

  private Circle createNewClickableCircle(Paint paint, int id, int pawnColumn, int pawnRow) {
    Circle actualCircle = new Circle(30, paint);
    actualCircle.setStroke(javafx.scene.paint.Color.BLACK);

    actualCircle.setOnMouseEntered(
        e -> {
          if (paint == Paint.valueOf(player)) {
            selectedCircle = actualCircle;
            selectedCircle.setFill(Paint.valueOf("BLUE"));
          }
        });

    actualCircle.setOnMouseExited(
        (e -> {
          selectedCircle = actualCircle;
          selectedCircle.setFill(paint);
        }));

    actualCircle.setOnMouseClicked(
        e -> {
          if (paint == Paint.valueOf(player)) {
            /* if there is a "previous" selected circle, first deselect it */
            if (selectedCircle != null) {
              selectedCircle.setFill(Paint.valueOf(player));
              game.deselectPiece();
            }
            /* (otherwise) select a new one */
            game.selectPieceById(id);
            List<Move> possibleMoves = game.getGameState().getSelectedPiece().getPossibleMoves();
            int moveCount = -1;
            for (Move move : possibleMoves) {
              moveCount++;
              Circle targetPositionCircle =
                  createNewTargetPositionCircle(
                      moveCount,
                      move.getNewCoordinate().getColumn(),
                      move.getNewCoordinate().getRow(),
                      pawnColumn,
                      pawnRow);
              chessBoardGridPane.add(
                  targetPositionCircle,
                  move.getNewCoordinate().getColumn(),
                  move.getNewCoordinate().getRow());
              GridPane.setHalignment(targetPositionCircle, HPos.CENTER);
            }
          }
        });
    return actualCircle;
  }

  // Special animated Method :P
  private Circle createNewTargetPositionCircle(
      int id, int targetColumn, int targetRow, int pawnColumn, int pawnRow) {
    Circle actualCircle = new Circle(10, Paint.valueOf("GREEN"));

    actualCircle.setOnMouseEntered(
        e -> {
          selectedCircle = actualCircle;
          selectedCircle.setFill(Paint.valueOf("BLUE"));
        });

    actualCircle.setOnMouseExited(
        (e -> {
          selectedCircle = actualCircle;
          selectedCircle.setFill(Paint.valueOf("GREEN"));
        }));

    actualCircle.setOnMouseClicked(
        e -> {
          actualCircle.setRadius(0.0);
          animateAndMove(id, pawnColumn, pawnRow, targetColumn, targetRow);

          // Rotation of the board, so that the currentPLayer always has the lower rows
          RotateTransition rt = new RotateTransition(Duration.millis(250), chessBoardGridPane);
          rt.setDelay(Duration.millis(1000));
          rt.setByAngle(180);
          rt.play();
        });
    return actualCircle;
  }

  private void animateAndMove(
      int id, int pawnColumn, int pawnRow, int targetColumn, int targetRow) {
    double moveX = (targetColumn - pawnColumn) * 75;
    double moveY = (targetRow - pawnRow) * 75;

    List<Node> myNodes =
        chessBoardGridPane.getChildren().stream()
            .filter(
                node ->
                    GridPane.getRowIndex(node) == pawnRow
                        && GridPane.getColumnIndex(node) == pawnColumn)
            .collect(Collectors.toList());

    KeyFrame f1 =
        new KeyFrame(
            Duration.seconds(1),
            new KeyValue(myNodes.get(0).translateXProperty(), moveX),
            new KeyValue(myNodes.get(0).translateYProperty(), moveY));
    Timeline tl = new Timeline();
    tl.getKeyFrames().add(f1);
    tl.play();
    tl.setOnFinished(e -> game.move(id));
  }
}
