package bauernschach.model;

import bauernschach.model.board.ChessBoard;
import bauernschach.model.board.ChessPiece;
import bauernschach.model.board.ChessPiece.Color;
import bauernschach.model.board.Move;
import java.util.List;

/**
 * The class stores the current state of the {@link Bauernschach} game, including the game status,
 * the chess board, the color of the current round, and the selected chess piece.
 */
public final class GameState {

  /**
   * Represents the status of the Bauernschach game.
   *
   * <ul>
   *   <li>{@code ONGOING}: if the game is still running.
   *   <li>{@code DRAW}: if there is no possible move left and no player has won.
   *   <li>{@code WHITE_WON}: if the player WHITE won the game.
   *   <li>{@code BLACK_WON}: if the player BLACK won the game.
   * </ul>
   */
  public enum GameStatus {
    ONGOING,
    DRAW,
    WHITE_WON,
    BLACK_WON
  }

  private final ChessBoard chessBoard;
  private final GameStatus gameStatus;

  private Color currentRound;
  private ChessPiece selectedPiece;

  /**
   * Contructs a GameState instance with a chess board of the given diemensions. Initially, the game
   * status is set to ONGOING, the first round is WHITE, and no piece is selected.
   *
   * @param numRows the number of rows of the chess board
   * @param numCols the number of rows of the chess board
   */
  GameState(int numRows, int numCols) {
    if (numRows == 0 || numCols == 0) {
      throw new IllegalArgumentException("Chess board dimensions cannot be 0.");
    }

    chessBoard = new ChessBoard(numRows, numCols);
    currentRound = Color.WHITE;
    gameStatus = GameStatus.ONGOING;
    selectedPiece = ChessPiece.NONE;
    updateCurrentRoundPossibleMoves();
    assert currentRoundHasPossibleMoves();
  }

  private GameState(
      ChessBoard chessBoard, Color currentRound, GameStatus gameStatus, ChessPiece selectedPiece) {
    this.chessBoard = chessBoard;
    this.currentRound = currentRound;
    this.gameStatus = gameStatus;
    this.selectedPiece = selectedPiece;
  }

  /**
   * Create a new instance of GameState where GameStatus is as given, other members are as in this
   * instance.
   */
  GameState with(GameStatus status) {
    return new GameState(chessBoard, currentRound, status, selectedPiece);
  }

  /** Returns whether the game is still running (game status = ONGOING). */
  public boolean isGameRunning() {
    return gameStatus == GameStatus.ONGOING;
  }

  /**
   * Starts a new round of the game, swaps the color of the current round and resets the selected
   * piece.
   */
  void newRound() {
    currentRound = currentRound.getOpposingColor();
    selectedPiece = ChessPiece.NONE;
    updateCurrentRoundPossibleMoves();
  }

  /** Returns whether a piece has been selected. */
  boolean hasSelectedPiece() {
    return !selectedPiece.isNone();
  }

  /** Returns the piece that has been selected. */
  public ChessPiece getSelectedPiece() {
    return selectedPiece;
  }

  /** Returns a clone of the current chess board. */
  public final ChessBoard getChessBoard() {
    return chessBoard.copyOf();
  }

  /** Returns the color of the current round. */
  public Color getCurrentRound() {
    return currentRound;
  }

  /** Returns the list of remaining chess pieces owned by the current round player. */
  List<ChessPiece> getPieceListAtCurrentRound() {
    return chessBoard.getImmutablePieceListByColor(currentRound);
  }

  /**
   * Returns the list of remaining chess pieces owned by the opposing player of the current round.
   */
  List<ChessPiece> getOpposingPieceListAtCurrentRound() {
    return chessBoard.getImmutablePieceListByColor(currentRound.getOpposingColor());
  }

  private void updateCurrentRoundPossibleMoves() {
    chessBoard.updatePossibleMovesByColor(currentRound);
  }

  /** Returns whether the is any possible move for the current round player. */
  boolean currentRoundHasPossibleMoves() {
    return chessBoard.hasPossibleMovesByColor(currentRound);
  }

  /** Selects the given piece. */
  void selectPiece(ChessPiece piece) {
    assert !hasSelectedPiece();
    assert getPieceListAtCurrentRound().contains(piece);
    selectedPiece = piece;
  }

  /** Deselects the selected piece. */
  void deselectPiece() {
    selectedPiece = ChessPiece.NONE;
  }

  /** Applies the given move. */
  void applyMove(Move move) {
    chessBoard.applyMove(selectedPiece, move);
  }

  /** Gets the current game status. */
  public GameStatus getStatus() {
    return gameStatus;
  }
}
