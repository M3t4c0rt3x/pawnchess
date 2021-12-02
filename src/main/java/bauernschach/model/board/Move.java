package bauernschach.model.board;

/**
 * This class represents the move of a {@link ChessPiece} on a {@link ChessBoard} of a {@link
 * Bauernschach} game.
 */
public final class Move {
  /** Represents the types of moves. */
  public enum MoveType {
    FORWARD,
    CAPTURE
  }

  final MoveType moveType;
  final Coordinate newCoordinate;
  final ChessPiece capturedPiece;

  /** Contructs a new Move instance that moves the piece forward. */
  public static Move newForwardMove(Coordinate coordinate) {
    return new Move(MoveType.FORWARD, coordinate, ChessPiece.NONE);
  }

  /** Contructs a new Move instance that captures an opposing piece. */
  public static Move newCaptureMove(Coordinate coordinate, ChessPiece chessPiece) {
    return new Move(MoveType.CAPTURE, coordinate, chessPiece);
  }

  private Move(MoveType moveType, Coordinate newCoordinate, ChessPiece capturedPiece) {
    this.moveType = moveType;
    this.newCoordinate = newCoordinate;
    this.capturedPiece = capturedPiece;
  }

  /** Gets the type of the move. */
  public MoveType getMoveType() {
    return moveType;
  }

  /** Gets the new coordinate of the piece after applying the move. */
  public Coordinate getNewCoordinate() {
    return newCoordinate;
  }

  /** Gets the opposing piece captured by the move. */
  public ChessPiece getCapturedPiece() {
    return capturedPiece;
  }
}
