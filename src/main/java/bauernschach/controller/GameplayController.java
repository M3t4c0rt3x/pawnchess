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
  private final ObjectProperty<Circle> selectedCircle = new SimpleObjectProperty<>();

  private final ObjectProperty<Point2D> selectedLocation = new SimpleObjectProperty<>();

  private static final PseudoClass SELECTED_P_C = PseudoClass.getPseudoClass("selected");


  @FXML
  private GridPane chessBoardGridPane;

  @FXML
  void initialize() {
    selectedCircle.addListener((obs, oldSelection, newSelection) -> {
      if (oldSelection != null) {
        oldSelection.pseudoClassStateChanged(SELECTED_P_C, false);
      }
      if (newSelection != null) {
        newSelection.pseudoClassStateChanged(SELECTED_P_C, true);
      }
    });
    displayCurrentBoard();
    System.out.println(selectedCircle.asString());
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
          if (currentPiece.getColor() == Color.BLACK) {
            chessBoardGridPane.add(createNewClickableBlackCircle(actualColumn, actualRow),
                actualColumn, actualRow);
          } else {
            chessBoardGridPane.add(createNewClickableWhiteCircle(actualColumn, actualRow),
                actualColumn, actualRow);
          }
        }
      }
    }
  }

  private Circle createNewClickableBlackCircle(int actualColumn, int actualRow) {
    Circle actualCircle = new Circle(30, Paint.valueOf("black"));
    actualCircle.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
      selectedCircle.set(actualCircle);
      selectedLocation.set(new Point2D(actualColumn, actualRow));
    });
    return actualCircle;
  }

  private Circle createNewClickableWhiteCircle(int actualColumn, int actualRow) {
    Circle actualCircle = new Circle(30, Paint.valueOf("white"));
    actualCircle.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
      selectedCircle.set(actualCircle);
      selectedLocation.set(new Point2D(actualColumn, actualRow));
    });
    return actualCircle;
  }
}
