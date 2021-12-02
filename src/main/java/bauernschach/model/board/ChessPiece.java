package bauernschach.model.board;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** This class represents the chess piece of a {@link Bauernschach} game. */
public final class ChessPiece {
  /** Represents the color of the piece. */
  public enum Color {
    BLACK {
      @Override
      public Color getOpposingColor() {
        return WHITE;
      }
    },
    WHITE {
      @Override
      public Color getOpposingColor() {
        return BLACK;
      }
    };

    public abstract Color getOpposingColor();
  }

  private final Color color;
  private final int id;
  private final Coordinate coordinate;
  private final List<Move> possibleMoves;

  /** A gloabl constant representing an empty dummy piece. */
  public static final ChessPiece NONE = new ChessPiece(null, -1, Coordinate.of(-1, -1), List.of());

  /** Creates a new chess piece. */
  public static ChessPiece create(Color color, int id, Coordinate coordinate) {
    return new ChessPiece(color, id, coordinate, new ArrayList<>());
  }

  private ChessPiece(Color color, int id, Coordinate coordinate, List<Move> possibleMoves) {
    this.color = color;
    this.id = id;
    this.coordinate = coordinate;
    this.possibleMoves = possibleMoves;
  }

  /** Returns a clone of the piece. */
  public ChessPiece copyOf() {
    if (isNone()) {
      return this;
    }
    return new ChessPiece(color, id, coordinate, List.copyOf(possibleMoves));
  }

  /**
   * Creates a new instance of ChessPiece where Coordinate is as given and with empty possbile
   * moves, other members are as in this instance.
   */
  public ChessPiece withNewPosition(Coordinate coordinate) {
    assert isValid();
    return new ChessPiece(color, id, coordinate, new ArrayList<>());
  }

  /** Update the possible moves of this piece. */
  public void updatePossibleMoves(ChessBoard chessBoard) {
    assert isValid();
    possibleMoves.clear();

    checkForwardMove(chessBoard);
    checkCaptureMove(chessBoard);
  }

  private void checkForwardMove(ChessBoard chessBoard) {
    final int directionX = (color == Color.WHITE) ? 1 : -1;
    final int newPositionY = coordinate.getColumn();

    for (int forwardStep = 1; forwardStep <= 2; ++forwardStep) {
      if (forwardStep == 2) {
        // check if the piece is at the starting row
        final int startRow = chessBoard.getStartRowByColor(color);
        if (coordinate.getRow() != startRow) {
          continue;
        }
      }

      final int newPositionX = coordinate.getRow() + directionX * forwardStep;
      Coordinate newCoordinate = Coordinate.of(newPositionX, newPositionY);
      // check if the new position is within bounds and not occupied
      if (chessBoard.isPositionWithinBounds(newCoordinate)
          && !chessBoard.hasPieceAt(newCoordinate)) {
        possibleMoves.add(Move.newForwardMove(newCoordinate));
      } else {
        // if 1 step forward is not possible, the 2 steps is not possible either
        break;
      }
    }
  }

  private void checkCaptureMove(ChessBoard chessBoard) {
    final int directionX = (color == Color.WHITE) ? 1 : -1;
    final int opponentPositionX = coordinate.getRow();
    final int newPositionX = coordinate.getRow() + directionX;

    // check the possiblity of capture move in right/left directions
    for (int directionY : List.of(-1, 1)) {
      final int opponentPositionY = coordinate.getColumn() + directionY;
      final int newPositionY = opponentPositionY;
      final Coordinate opponentCoordinate = Coordinate.of(opponentPositionX, opponentPositionY);
      final Coordinate newCoordinate = Coordinate.of(newPositionX, newPositionY);
      if (chessBoard.isPositionWithinBounds(opponentCoordinate)
          && chessBoard.hasOpposingPieceAt(opponentCoordinate, color)
          && chessBoard.isPositionWithinBounds(newCoordinate)
          && !chessBoard.hasPieceAt(newCoordinate)) {
        ChessPiece capturedPiece = chessBoard.getPieceAt(opponentCoordinate);
        possibleMoves.add(Move.newCaptureMove(newCoordinate, capturedPiece));
      }
    }
  }

  /** Returns whether the piece has any possible moves. */
  public boolean hasPossibleMoves() {
    return !possibleMoves.isEmpty();
  }

  /** Get the color of this chess piece. */
  public Color getColor() {
    return color;
  }

  /** Get the ID of this chess piece. */
  public int getId() {
    return id;
  }

  /** Get the coordinate of this chess piece. */
  public Coordinate getCoordinate() {
    return coordinate;
  }

  private boolean isValid() {
    return (color != null)
        && (id >= 0)
        && (coordinate.getRow() >= 0)
        && (coordinate.getColumn() >= 0);
  }

  /**
   * Returns whether the piece is equal to the global dummy piece, insted of an actual playing
   * piece.
   */
  public boolean isNone() {
    return this == NONE;
  }

  /** Gets the list of possible moves. */
  public List<Move> getPossibleMoves() {
    return List.copyOf(possibleMoves);
  }

  /** Gets the list of new coordinates after applying the possible moves. */
  public List<Coordinate> getPossibleMoveCoordinates() {
    List<Coordinate> coordList = new ArrayList<>();
    for (Move move : possibleMoves) {
      coordList.add(move.getNewCoordinate());
    }
    return coordList;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ChessPiece that = (ChessPiece) o;
    return color == that.color && id == that.id && coordinate.equals(that.coordinate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(color, id, coordinate);
  }

  @Override
  public String toString() {
    return "ChessPiece{" + "color=" + color + ", id=" + id + ", coordinate=" + coordinate + '}';
  }
}
