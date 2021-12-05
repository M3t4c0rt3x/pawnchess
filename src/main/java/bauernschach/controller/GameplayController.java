package bauernschach.controller;

import bauernschach.model.Bauernschach;
import bauernschach.model.Bauernschach.OperationStatus;
import bauernschach.model.GameState;
import bauernschach.model.GameState.GameStatus;
import bauernschach.model.board.ChessPiece;
import bauernschach.model.board.ChessPiece.Color;
import bauernschach.model.board.Coordinate;
import bauernschach.model.board.Move;
import bauernschach.model.observable.Observer;
import java.io.IOException;
import java.util.List;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class GameplayController implements Observer {

  private Bauernschach game = new Bauernschach();
  private Circle selectedCircle;
  static String player;

  @FXML
  private GridPane chessBoardGridPane;

  @FXML
  private Label currentPlayerLabel;

  @FXML
  void initialize() {
    game.subscribe(this);
    player = game.getGameState().getCurrentRound().toString();
    currentPlayerLabel.setText("current player: " + player.toUpperCase());
    displayCurrentBoard();
  }


  @Override
  public void updateState(GameState state) {
    player = game.getGameState().getCurrentRound().toString();
    final GameState gameState = game.getGameState();
    final GameState.GameStatus gameStatus = gameState.getStatus();

    //if either white or black has won
    if ((gameStatus == GameState.GameStatus.WHITE_WON) || (gameStatus
        == GameState.GameStatus.BLACK_WON)) {
      FXMLLoader fxmlLoader =
          new FXMLLoader(getClass().getResource("/VictoryScreen.fxml"));
      try {
        Parent root = fxmlLoader.load();
        Stage gameplayScene = new Stage();
        gameplayScene.setTitle("Bauernschach");
        gameplayScene.setScene(new Scene(root, 800, 600));
        gameplayScene.setResizable(false);
        gameplayScene.show();
        //((Node) (event.getSource())).getScene().getWindow().hide();
      } catch (IOException e) {
        e.getCause().getCause().printStackTrace();
      }
      //or the game is ongoing
    } else {
      currentPlayerLabel.setText("current player: " + player.toUpperCase());
      displayCurrentBoard();
    }
  }

  private void displayCurrentBoard() {
    final GameState gameState = game.getGameState();
    chessBoardGridPane.getChildren().clear();

    for (int actualRow = 0; actualRow < 8; actualRow++) {
      for (int actualColumn = 0; actualColumn < 8; actualColumn++) {
        ChessPiece currentPiece = gameState.getChessBoard()
            .getPieceAt(Coordinate.of(actualRow, actualColumn));
        // hier Einfärbung einfügen -> chessBoardGridPane.add()
        if (currentPiece.getColor() != null) {
          int id = currentPiece.getId();
          if (currentPiece.getColor() == Color.BLACK) {
            Circle currentCircle = createNewClickableCircle(Paint.valueOf("BLACK"), id);
            chessBoardGridPane.add(currentCircle, actualColumn, actualRow);
            GridPane.setHalignment(currentCircle, HPos.CENTER);
          } else {
            Circle currentCircle = createNewClickableCircle(Paint.valueOf("WHITE"), id);
            chessBoardGridPane.add(currentCircle, actualColumn, actualRow);
            GridPane.setHalignment(currentCircle, HPos.CENTER);
          }
        }
      }
    }
  }

  private Circle createNewClickableCircle(Paint paint, int id) {
    Circle actualCircle = new Circle(30, paint);
    actualCircle.setStroke(javafx.scene.paint.Color.BLACK);
    actualCircle.setOnMouseEntered(e -> {
      if (paint == Paint.valueOf(player)) {
        selectedCircle = actualCircle;
        selectedCircle.setFill(Paint.valueOf("BLUE"));
      }
    });
    actualCircle.setOnMouseExited((e -> {
      selectedCircle = actualCircle;
      selectedCircle.setFill(paint);
    }));
    actualCircle.setOnMouseClicked(e -> {
      if (paint == Paint.valueOf(player)) {
        if (selectedCircle != null) {
          selectedCircle.setFill(Paint.valueOf(player));
          game.deselectPiece();
        }
        /*selectedCircle = actualCircle;
        selectedCircle.setFill(Paint.valueOf("BLUE"));*/
        game.selectPieceById(id);
        List<Move> possibleMoves = game.getGameState().getSelectedPiece().getPossibleMoves();
        int moveCount = -1;
        for (Move move : possibleMoves) {
          moveCount++;
          //System.out.println(move.getNewCoordinate().toString());
          Circle targetPositionCircle = createNewTargetPositionCircle(moveCount,
              move.getNewCoordinate().getRow(), move.getNewCoordinate().getColumn());
          chessBoardGridPane.add(targetPositionCircle,
              move.getNewCoordinate().getColumn(), move.getNewCoordinate().getRow());
          GridPane.setHalignment(targetPositionCircle, HPos.CENTER);
        }
      }
    });
    return actualCircle;
  }

  private Circle createNewTargetPositionCircle(int id, int row, int column) {
    Circle actualCircle = new Circle(10, Paint.valueOf("GREEN"));
    actualCircle.setOnMouseEntered(e -> {
      selectedCircle = actualCircle;
      selectedCircle.setFill(Paint.valueOf("BLUE"));
    });
    actualCircle.setOnMouseExited((e -> {
      selectedCircle = actualCircle;
      selectedCircle.setFill(Paint.valueOf("GREEN"));
    }));
    actualCircle.setOnMouseClicked(e -> {
      OperationStatus operationStatus = game.move(id);
    });
    return actualCircle;
  }
}
