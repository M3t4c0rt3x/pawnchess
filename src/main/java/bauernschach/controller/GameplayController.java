package bauernschach.controller;

import bauernschach.model.Bauernschach;
import bauernschach.model.GameState;
import bauernschach.model.board.ChessBoard;
import bauernschach.model.board.ChessPiece;
import bauernschach.model.board.Coordinate;
import bauernschach.model.observable.Observer;
import java.io.IOException;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;

public class GameplayController implements Observer {
  private Bauernschach game = new Bauernschach();

  @FXML
  void initialize() {
    /*public void handle(ActionEvent event){
      FXMLLoader fxmlLoader =
          new FXMLLoader(getClass().getResource("/GameplayScreen.fxml"));
      final Arc pawn = new Arc(10, 10, 50, 50, 45, 270);
      pawn.setType(ArcType.ROUND);
      Arc blackPawn = new pawn.setFill(Color.BLACK);
      Arc whitePawn = new pawn.setFill(Color.WHITE);

      final FlowPane flowPane = new FlowPane();
      flowPane.getChildren().addAll(blackPawn);
      primaryStage.setScene(new Scene(flowPane, 300, 130));
      primaryStage.setTitle(this.getClass().getSimpleName());
      primaryStage.show();
    } */
    displayCurrentBoard();
  }

  @FXML
  private GridPane chessBoardGridPane;

  @Override
  public void updateState(GameState state) {
  }

  private void displayCurrentBoard () {
    final GameState gameState = game.getGameState();
    final ChessBoard chessBoard = gameState.getChessBoard();

    int numColums = gameState.getChessBoard().getNumColumns();
    int numRows = gameState.getChessBoard().getNumRows();
    for (int actualRow = 0; actualRow < numRows; actualRow++) {
      for (int actualColumn = 0; actualColumn < numColums; actualColumn++) {
        ChessPiece currentPiece = gameState.getChessBoard().getPieceAt(Coordinate.of(actualRow, actualColumn));
        if (currentPiece.getColor() != null) {
          System.out.println(currentPiece);
          //chessBoardGridPane.add(new Circle(25, Color.BLACK), actualRow, actualColumn);
        }
      }
    }
  }
}
