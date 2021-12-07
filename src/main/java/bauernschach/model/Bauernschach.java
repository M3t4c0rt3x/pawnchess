package bauernschach.model;

import bauernschach.model.board.ChessPiece;
import bauernschach.model.board.Move;
import bauernschach.model.observable.Observable;
import bauernschach.model.observable.Observer;
import bauernschach.model.observable.ObserverSupport;

/** Represents a Bauernschach game. This class implements the game logic. */
public class Bauernschach implements Observable {

  /**
   * Represent the status of the previous game operation.
   *
   * <ul>
   *   <li>{@code SUCCESS}: if the operation was executed sucessfully.
   *   <li>{@code FAIL}: if the operation could not be done for some reasons, e.g. the game is not
   *       running.
   * </ul>
   */
  public enum OperationStatus {
    SUCCESS,
    FAIL
  }

  static final int DEFAULT_NUM_ROWS = 8;
  static final int DEFAULT_NUM_COLS = 8;

  private GameState gameState;
  private final ObserverSupport observerSupport = new ObserverSupport();

  /** Contruct a Bauernschach game instance with the prespecified board dimensions. */
  public Bauernschach() {
    gameState = new GameState(DEFAULT_NUM_ROWS, DEFAULT_NUM_COLS);
  }

  /** Contruct a Bauernschach game instance with the given board dimensions. */
  public Bauernschach(int numRows, int numCols) {
    gameState = new GameState(numRows, numCols);
  }

  /** Get the current GameState. */
  public final GameState getGameState() {
    return gameState;
  }

  /**
   * Selects the chess piece by its ID.
   *
   * @param id the chess piece ID
   * @return OperationStatus.SUCCESS if the piece with the given ID exists and is selected;
   *     OperationStatus.FAIL if the game is not running, another piece has already been selected,
   *     or the given ID does not match any moveable piece
   */
  public OperationStatus selectPieceById(int id) {
    if (!gameState.isGameRunning() || gameState.hasSelectedPiece()) {
      return OperationStatus.FAIL;
    }

    for (ChessPiece piece : gameState.getPieceListAtCurrentRound()) {
      if (!piece.hasPossibleMoves()) {
        continue;
      }
      if (piece.getId() == id) {
        gameState.selectPiece(piece);
        notifyAboutState(gameState);
        assert gameState.isGameRunning();
        return OperationStatus.SUCCESS;
      }
    }
    return OperationStatus.FAIL;
  }

  /**
   * Deselects the chess piece.
   *
   * @return OperationStatus.SUCCESS if a piece is selected; OperationStatus.FAIL if the game is not
   *     running, or no piece has been selected
   */
  public OperationStatus deselectPiece() {
    if (!gameState.isGameRunning() || !gameState.hasSelectedPiece()) {
      return OperationStatus.FAIL;
    }
    gameState.deselectPiece();
    notifyAboutState(gameState);
    assert gameState.isGameRunning();
    return OperationStatus.SUCCESS;
  }

  private void startNewRound() {
    int count = 0;
    do {
      gameState.newRound();
      ++count;
    } while ((count < 2) && !gameState.currentRoundHasPossibleMoves());

    if (!gameState.currentRoundHasPossibleMoves()) {
      gameState = gameState.with(GameState.GameStatus.DRAW);
      notifyAboutState(gameState);
    }
  }

  private void checkWinningConditions(Move move) {
    final int finishRow =
        gameState.getChessBoard().getFinishRowByColor(gameState.getCurrentRound());

    // reach finish row or no opposing piece left
    if ((move.getNewCoordinate().getRow() == finishRow)
        || gameState.getOpposingPieceListAtCurrentRound().isEmpty()) {
      GameState.GameStatus status =
          (gameState.getCurrentRound() == ChessPiece.Color.WHITE)
              ? GameState.GameStatus.WHITE_WON
              : GameState.GameStatus.BLACK_WON;
      gameState = gameState.with(status);
      notifyAboutState(gameState);
    }
  }

  /**
   * Applies the move, checks winning conditions, and updates the game status. If the game is still
   * running, i.e. no player has won and there exists possible moves, starts a new round.
   *
   * @param id the move ID
   * @return OperationStatus.SUCCESS if the selected move is applied; OperationStatus.FAIL if the
   *     game is not running, or the move with the given ID does not exist
   */
  public OperationStatus move(int id) {
    if (!gameState.isGameRunning()) {
      return OperationStatus.FAIL;
    }
    if (id < 0 || id >= gameState.getSelectedPiece().getPossibleMoves().size()) {
      return OperationStatus.FAIL;
    }
    Move move = gameState.getSelectedPiece().getPossibleMoves().get(id);
    gameState.applyMove(move);

    checkWinningConditions(move);

    if (gameState.isGameRunning()) {
      startNewRound();
    }
    notifyAboutState(gameState);
    return OperationStatus.SUCCESS;
  }

  /**
   * Pass the current round and starts a new round.
   *
   * @return OperationStatus.FAIL if the game is not running; OperationStatus.SUCCESS otherwise
   */
  public OperationStatus pass() {
    if (!gameState.isGameRunning()) {
      return OperationStatus.FAIL;
    }
    startNewRound();
    notifyAboutState(gameState);
    assert !gameState.hasSelectedPiece();
    assert gameState.isGameRunning();
    return OperationStatus.SUCCESS;
  }

  @Override
  public void subscribe(Observer obsv) {
    observerSupport.subscribe(obsv);
  }

  @Override
  public void unsubscribe(Observer obsv) {
    observerSupport.unsubscribe(obsv);
  }

  @Override
  public void notifyAboutState(GameState newState) {
    observerSupport.notifyAboutState(newState);
  }
}
