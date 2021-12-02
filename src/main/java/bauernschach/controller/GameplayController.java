package bauernschach.controller;

import bauernschach.model.Bauernschach;
import bauernschach.model.GameState;
import bauernschach.model.board.ChessBoard;
import bauernschach.model.board.ChessPiece;
import bauernschach.model.board.ChessPiece.Color;
import bauernschach.model.board.Coordinate;
import bauernschach.model.observable.Observer;
import java.awt.ScrollPane;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.List;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class GameplayController implements Observer {

  private Bauernschach game = new Bauernschach();
  private Circle selectedCircle;

  @FXML
  private GridPane chessBoardGridPane;

  @FXML
  void initialize() {
    displayCurrentBoard();
  }


  @Override
  public void updateState(GameState state) {
  }

  private void displayCurrentBoard() {
    final GameState gameState = game.getGameState();
    final ChessBoard chessBoard = gameState.getChessBoard();

    int numColums = gameState.getChessBoard().getNumColumns();
    int numRows = gameState.getChessBoard().getNumRows();
    for (int actualRow = 0; actualRow < numRows; actualRow++) {
      for (int actualColumn = 0; actualColumn < numColums; actualColumn++) {
        ChessPiece currentPiece = gameState.getChessBoard()
            .getPieceAt(Coordinate.of(actualRow, actualColumn));
        if (currentPiece.getColor() != null) {
          System.out.println(currentPiece);
          int id = currentPiece.getId();
          if (currentPiece.getColor() == Color.BLACK) {
            chessBoardGridPane.add(createNewClickableCircle(Paint.valueOf("black"), id),
                actualColumn, actualRow);
          } else {
            chessBoardGridPane.add(createNewClickableCircle(Paint.valueOf("white"), id),
                actualColumn, actualRow);
          }
        }
      }
    }
  }

  private Circle createNewClickableCircle(Paint paint, int id) {
    Circle actualCircle = new Circle(30, paint);
    actualCircle.setOnMouseClicked(e -> {
      selectedCircle = actualCircle;
      selectedCircle.setFill(Paint.valueOf("blue"));
      game.selectPieceById(id);
      System.out.println("Paint:" + paint.toString() + ", ID: " + id);
    });
    return actualCircle;
  }
}
