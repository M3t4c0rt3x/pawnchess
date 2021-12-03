package bauernschach.controller;

import bauernschach.model.Bauernschach;
import bauernschach.model.GameState;
import bauernschach.model.board.ChessBoard;
import bauernschach.model.board.ChessPiece;
import bauernschach.model.board.ChessPiece.Color;
import bauernschach.model.board.Coordinate;
import bauernschach.model.board.Move;
import bauernschach.model.observable.Observer;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

public class GameplayController implements Observer {

  private Bauernschach game = new Bauernschach();
  private Circle selectedCircle;
  private String player;

  @FXML
  private GridPane chessBoardGridPane;

  @FXML
  void initialize() {
    player = game.getGameState().getCurrentRound().toString();
    System.out.println(player);
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
    actualCircle.setOnMouseClicked(e -> {
      if (paint == paint.valueOf(player)) {
        if (selectedCircle != null) {
          selectedCircle.setFill(Paint.valueOf(player));
          game.deselectPiece();
        }
        selectedCircle = actualCircle;
        selectedCircle.setFill(Paint.valueOf("BLUE"));
        game.selectPieceById(id);
        List<Move> possibleMoves = game.getGameState().getSelectedPiece().getPossibleMoves();
        System.out.println("Paint:" + paint.toString() + ", ID: " + id);
        for (Move move : possibleMoves) {
          System.out.println(move.getNewCoordinate().toString());
          Circle targetPositionCircle = createNewTargetPositionCircle(id, move.getNewCoordinate().getRow(), move.getNewCoordinate().getColumn());
          chessBoardGridPane.add(targetPositionCircle,
              move.getNewCoordinate().getColumn(), move.getNewCoordinate().getRow());
          GridPane.setHalignment(targetPositionCircle, HPos.CENTER);
        }
        }
      });
    return actualCircle;
  }

  private Circle createNewTargetPositionCircle(int id, int row, int column) {
    Circle actualCircle = new Circle(10, Paint.valueOf("green"));
    actualCircle.setOnMouseClicked(e -> {
          game.getGameState().getSelectedPiece().withNewPosition(Coordinate.of(row, column));
          game.move(id);
    });
    return actualCircle;
  }
}
