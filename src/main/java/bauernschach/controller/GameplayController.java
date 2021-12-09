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
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Controller for connecting the GameplayScreen with the model Bauernschach / Gamestate.
 */
public class GameplayController implements Observer {

  private Bauernschach game = new Bauernschach();
  private Rectangle selectedRectangle;
  private String player;
  private Image blackAvatar;
  private Image whiteAvatar;
  private Image selectedBlackAvatar;
  private Image selectedWhiteAvatar;

  @FXML
  private GridPane chessBoardGridPane;

  @FXML
  private Label currentPlayerLabel;

  @FXML
  private Button passButton;

  /**
   * Start method of the GameplayController to set all things up (especially subsrcibe to the game,
   *    load all images, rotate it to 'white down' for the first player and show the chessboard.
   */

  @FXML
  void initialize() {
    game.subscribe(this);
    player = game.getGameState().getCurrentRound().toString();
    currentPlayerLabel.setText("current player: " + player.toUpperCase());

    // try to load avatar images
    try {
      blackAvatar = new Image(getClass().getResource("/images/blackPawn.png").toString());
      whiteAvatar = new Image(getClass().getResource("/images/whitePawn.png").toString());
      selectedBlackAvatar = new Image(getClass().getResource("/images/selectedBlackPawn.png").toString());
      selectedWhiteAvatar = new Image(getClass().getResource("/images/selectedWhitePawn.png").toString());
    } catch (IllegalArgumentException e) {
      System.err.println("Image not found.");
    }

    // Rotation of the board, so that the currentPLayer always has the lower rows
    RotateTransition rt = new RotateTransition(Duration.millis(1000), chessBoardGridPane);
    rt.setByAngle(180);
    rt.play();

    displayCurrentBoard();
  }

  /**
   * Method checks if the game is won or not. When won it delegates to a follwing victory Screen, if
   * not (game is ongoing) it refreshes the board (reload all pawns on their positions)
   *
   * @param state gamestate to represent the current state of the game
   */

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
   * Method to display all pawns at the board and delegating all further operations to sub-methods.
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
            Rectangle currentCircle =
                createNewClickablePawn(Paint.valueOf("BLACK"), id, actualColumn, actualRow);
            chessBoardGridPane.add(currentCircle, actualColumn, actualRow);
            GridPane.setHalignment(currentCircle, HPos.CENTER);
          } else {
            Rectangle currentCircle =
                createNewClickablePawn(Paint.valueOf("WHITE"), id, actualColumn, actualRow);
            chessBoardGridPane.add(currentCircle, actualColumn, actualRow);
            GridPane.setHalignment(currentCircle, HPos.CENTER);
          }
        }
      }
    }
  }

  /**
   * Creates a single pawn with a given color and clickable to calculate and show possible moves
   *
   * @param paint is the color for this pawn
   * @param id is the id of the pawn
   * @param pawnColumn is the column position of this pawn on the board
   * @param pawnRow is the row position of this pawn on the board
   * @return Rectangle containing a pawn with a given color
   */

  private Rectangle createNewClickablePawn(Paint paint, int id, int pawnColumn, int pawnRow) {
    Rectangle actualRectangle;
    if (blackAvatar != null && whiteAvatar != null) {
      if (paint == Paint.valueOf("BLACK")) {
        actualRectangle = new Rectangle(74,74);
        actualRectangle.setFill(new ImagePattern(blackAvatar));
      } else {
        actualRectangle = new Rectangle(74,74);
        actualRectangle.setFill(new ImagePattern(whiteAvatar));
        actualRectangle.setRotate(180);
      }
    } else {
      actualRectangle = new Rectangle(55, 55, paint);
    }

    actualRectangle.setOnMouseEntered(
        e -> {
          if (paint == Paint.valueOf(player)) {
            selectedRectangle = actualRectangle;
            if (paint == Paint.valueOf("BLACK")) {
              selectedRectangle.setFill(new ImagePattern(selectedBlackAvatar));
            } else {
              selectedRectangle.setFill(new ImagePattern(selectedWhiteAvatar));
            }
          }
        });

    actualRectangle.setOnMouseExited(
        (e -> {
          selectedRectangle = actualRectangle;
          if (paint == Paint.valueOf("BLACK")) {
            selectedRectangle.setFill(new ImagePattern(blackAvatar));
          } else {
            selectedRectangle.setFill(new ImagePattern(whiteAvatar));
          }
        }));

    actualRectangle.setOnMouseClicked(
        e -> {
          if (paint == Paint.valueOf(player)) {
            /* if there is a "previous" selected pawn, first deselect it */
            if (selectedRectangle != null) {
              if (paint == Paint.valueOf("BLACK")) {
                selectedRectangle.setFill(new ImagePattern(blackAvatar));
              } else {
                selectedRectangle.setFill(new ImagePattern(whiteAvatar));
              }
              game.deselectPiece();
            }
            /* (otherwise) select a new pawn */
            if (paint == Paint.valueOf("BLACK")) {
              selectedRectangle.setFill(new ImagePattern(selectedBlackAvatar));
            } else {
              selectedRectangle.setFill(new ImagePattern(selectedWhiteAvatar));
            }
            game.selectPieceById(id);
            List<Move> possibleMoves = game.getGameState().getSelectedPiece().getPossibleMoves();
            int moveCount = -1;
            for (Move move : possibleMoves) {
              moveCount++;
              Rectangle targetPositionCell = createNewTargetPositionCell(
                  moveCount,
                  move.getNewCoordinate().getColumn(),
                  move.getNewCoordinate().getRow(),
                  pawnColumn,
                  pawnRow);
              chessBoardGridPane.add(
                  targetPositionCell,
                  move.getNewCoordinate().getColumn(),
                  move.getNewCoordinate().getRow());
              GridPane.setHalignment(targetPositionCell, HPos.CENTER);
            }
          }
        });
    return actualRectangle;
  }

  /**
   * Creates a single target cell in the chessboard where a pawn can move to and which is clickable.
   *
   * @param id is the move id, so that the model can do the right move
   * @param targetColumn is the target column of the pawn
   * @param targetRow is the target row of the pawn
   * @param pawnColumn is the current column of the pawn
   * @param pawnRow is the current row of the pawn
   * @return Rectangle, which is clickable to proceed the move
   */
  private Rectangle createNewTargetPositionCell(
      int id, int targetColumn, int targetRow, int pawnColumn, int pawnRow) {
    Rectangle actualRectangle = new Rectangle(75, 75);
    actualRectangle.setFill(Paint.valueOf("TRANSPARENT"));
    actualRectangle.setStroke(Paint.valueOf("GREEN"));
    actualRectangle.setStrokeWidth(3.0);

    actualRectangle.setOnMouseEntered(
        e -> {
          selectedRectangle = actualRectangle;
          selectedRectangle.setStroke(Paint.valueOf("RED"));
        });

    actualRectangle.setOnMouseExited(
        (e -> {
          selectedRectangle = actualRectangle;
          selectedRectangle.setStroke(Paint.valueOf("GREEN"));
        }));

    actualRectangle.setOnMouseClicked(
        e -> {
          animateAndMove(id, pawnColumn, pawnRow, targetColumn, targetRow);

          // Rotate the board, so that the currentPLayer always has the lower rows
          RotateTransition rt = new RotateTransition(Duration.millis(250), chessBoardGridPane);
          rt.setDelay(Duration.millis(1000));
          rt.setByAngle(180);
          rt.play();
        });
    return actualRectangle;
  }

  /**
   * Makes an animated move of a pawn to the selectedTarget cell.
   *
   * @param id is the move id, so the model can do the right move
   * @param pawnColumn is the current column of the pawn
   * @param pawnRow is the current column of the pawn
   * @param targetColumn is the target column of the pawn
   * @param targetRow is the target row of the pawn
   */
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
