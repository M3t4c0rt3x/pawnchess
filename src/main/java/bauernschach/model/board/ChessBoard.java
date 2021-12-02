package bauernschach.model.board;

import bauernschach.model.board.ChessPiece.Color;
import java.util.ArrayList;
import java.util.List;

/** This class represents the playing field of the {@link Bauernschach} game. */
public final class ChessBoard {
  private final int numRows;
  private final int numCols;
  private final ChessPiece[][] board;
  private final List<ChessPiece> whiteChessPieces;
  private final List<ChessPiece> blackChessPieces;

  /** Contructs a chess board with the given dimension. */
  public ChessBoard(int numRows, int numCols) {
    if (numRows == 0 || numCols == 0) {
      throw new IllegalArgumentException("Chess board dimensions cannot be 0.");
    }

    this.numRows = numRows;
    this.numCols = numCols;
    board = new ChessPiece[numRows][numCols];
    whiteChessPieces = new ArrayList<>();
    blackChessPieces = new ArrayList<>();

    for (int i = 0; i < numRows; ++i) {
      for (int j = 0; j < numCols; ++j) {
        Coordinate coordinate = Coordinate.of(i, j);
        if (i == getStartRowByColor(Color.WHITE)) {
          ChessPiece piece = ChessPiece.create(Color.WHITE, whiteChessPieces.size(), coordinate);
          whiteChessPieces.add(piece);
          setPieceAt(piece, coordinate);
        } else if (i == getStartRowByColor(Color.BLACK)) {
          ChessPiece piece = ChessPiece.create(Color.BLACK, blackChessPieces.size(), coordinate);
          blackChessPieces.add(piece);
          setPieceAt(piece, coordinate);
        } else { // no chess initially
          setPieceAt(ChessPiece.NONE, coordinate);
        }
      }
    }
  }

  private ChessBoard(ChessBoard sourceBoard) {
    numRows = sourceBoard.getNumRows();
    numCols = sourceBoard.getNumColumns();
    board = new ChessPiece[numRows][numCols];
    List<ChessPiece> clonedWhitePieceList = new ArrayList<>();
    List<ChessPiece> clonedBlackPieceList = new ArrayList<>();

    for (int i = 0; i < numRows; ++i) {
      for (int j = 0; j < numCols; ++j) {
        Coordinate coordinate = Coordinate.of(i, j);
        ChessPiece clonedPiece = sourceBoard.getPieceAt(coordinate).copyOf();
        setPieceAt(clonedPiece, coordinate);
        if (clonedPiece.getColor() == Color.WHITE) {
          clonedWhitePieceList.add(clonedPiece);
        } else if (clonedPiece.getColor() == Color.BLACK) {
          clonedBlackPieceList.add(clonedPiece);
        }
      }
    }

    whiteChessPieces = List.copyOf(clonedWhitePieceList);
    blackChessPieces = List.copyOf(clonedBlackPieceList);
  }

  public ChessBoard copyOf() {
    return new ChessBoard(this);
  }

  /** Places the piece at the given position. */
  private void setPieceAt(ChessPiece piece, Coordinate coordinate) {
    assert piece.isNone() || (piece.getCoordinate().equals(coordinate));
    board[coordinate.getRow()][coordinate.getColumn()] = piece;
  }

  /** Gets the piece at the given position. */
  public ChessPiece getPieceAt(Coordinate coordinate) {
    return board[coordinate.getRow()][coordinate.getColumn()];
  }

  /** Gets the number of rows of the board. */
  public int getNumRows() {
    return numRows;
  }

  /** Gets the number of columns of the board. */
  public int getNumColumns() {
    return numCols;
  }

  /** Returns whether the given position is with the board's bounds. */
  boolean isPositionWithinBounds(Coordinate coordinate) {
    final int positionX = coordinate.getRow();
    final int positionY = coordinate.getColumn();
    return (positionX >= 0)
        && (positionX < getNumRows())
        && (positionY >= 0)
        && (positionY < getNumColumns());
  }

  /** Returns whether there is a piece placed at the given position. */
  boolean hasPieceAt(Coordinate coordinate) {
    return getPieceAt(coordinate) != ChessPiece.NONE;
  }

  /** Returns whether there is an opposing piece placed at the given position. */
  boolean hasOpposingPieceAt(Coordinate coordinate, Color color) {
    return hasPieceAt(coordinate) && getPieceAt(coordinate).getColor() != color;
  }

  /** Returns the starting row of the given color. */
  int getStartRowByColor(Color color) {
    return (color == Color.WHITE) ? 0 : (getNumRows() - 1);
  }

  /** Returns the finishing row of the given color. */
  public int getFinishRowByColor(Color color) {
    return getStartRowByColor(color.getOpposingColor());
  }

  /** Returns the list of remaining chess pieces with the given color. */
  private List<ChessPiece> getPieceListByColor(Color color) {
    return (color == Color.WHITE) ? whiteChessPieces : blackChessPieces;
  }

  /** Returns the remaining chess pieces with the given color. */
  public List<ChessPiece> getImmutablePieceListByColor(Color color) {
    List<ChessPiece> clonedList = new ArrayList<>();
    for (ChessPiece piece : getPieceListByColor(color)) {
      clonedList.add(piece.copyOf());
    }
    return List.copyOf(clonedList);
  }

  /** Applies the given move to the given chess piece. */
  public void applyMove(ChessPiece piece, Move move) {
    movePiece(piece, move.getNewCoordinate());

    if (move.getMoveType() == Move.MoveType.CAPTURE) {
      ChessPiece capturedPiece = move.getCapturedPiece();
      removePiece(capturedPiece);
    }
  }

  /** Moves the piece to the new position. */
  private void movePiece(ChessPiece piece, Coordinate newCoordinate) {
    assert !piece.getCoordinate().equals(newCoordinate);
    removePiece(piece);
    ChessPiece movedPiece = piece.withNewPosition(newCoordinate);
    setPieceAt(movedPiece, newCoordinate);
    getPieceListByColor(movedPiece.getColor()).add(movedPiece);
  }

  /** Removes the piece from the chess board. */
  private void removePiece(ChessPiece piece) {
    assert !piece.isNone();
    setPieceAt(ChessPiece.NONE, piece.getCoordinate());
    assert getPieceListByColor(piece.getColor()).remove(piece);
  }

  /** Update the possible moves of the chess pieces with the given color. */
  public void updatePossibleMovesByColor(Color color) {
    for (ChessPiece piece : getPieceListByColor(color)) {
      piece.updatePossibleMoves(this);
    }
  }

  /** Returns whether the is any possible move for the player of the given color. */
  public boolean hasPossibleMovesByColor(Color color) {
    for (ChessPiece piece : getImmutablePieceListByColor(color)) {
      if (piece.hasPossibleMoves()) {
        return true;
      }
    }
    return false;
  }
}
