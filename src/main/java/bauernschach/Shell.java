package bauernschach;

import bauernschach.model.Bauernschach;
import bauernschach.model.Bauernschach.OperationStatus;
import bauernschach.model.GameState;
import bauernschach.model.board.ChessBoard;
import bauernschach.model.board.ChessPiece;
import bauernschach.model.board.Coordinate;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * A Shell interface for a {@link Bauernschach} game. This class handles user's input and displays
 * the chess board.
 */
public class Shell {
  enum PrintMode {
    PIECE_SELECT,
    MOVE_SELECT,
    PLAIN
  }

  private static final String PROMPT = "BS > ";

  private static final String NEWGAME_COMMAND = "NEWGAME";
  private static final String PRINT_COMMAND = "PRINT";
  private static final String SELECT_COMMAND = "SELECT";
  private static final String DESELECT_COMMAND = "DESELECT";
  private static final String MOVE_COMMAND = "MOVE";
  private static final String PASS_COMMAND = "PASS";
  private static final String QUIT_COMMAND = "QUIT";
  private static final String HELP_COMMAND = "HELP";

  private static final String NO_COMMAND_MESSAGE = "No Command given.";
  private static final String INVALID_COMMAND_MESSAGE = "Invalid command.";
  private static final String INVALID_ARGUMENTS_MESSAGE = "Invalid arguments.";
  private static final String INVALID_INPUT_MESSAGE = "Invalid input.";
  private static final String HELP_MESSAGE =
      "Commands:\n"
          + "- NEWGAME: create a new Bauernschach game\n"
          + "- PRINT: print the current chess board\n"
          + "- SELECT <int chess_id>: select the chess piece by ID\n"
          + "- DESELECT: deselect the selected chess piece\n"
          + "- MOVE <int move_id>: move the selected chess according to the chosen move\n"
          + "- PASS: pass the current round\n"
          + "- QUIT: quit the shell\n"
          + "- HELP: print the help message";

  private static final String NO_ACTIVE_GAME_MESSAGE = "No active game running.";
  private static final String NOT_POSSIBLE_MESSAGE = "Not possible.";
  private static final String SELECT_PIECE_MESSAGE = "Please select a chess piece.";
  private static final String SELECT_MOVE_MESSAGE = "Please select a move.";

  private static final char ROW_START_CHAR = 'A';
  private static final int COLUMN_DISPLAY_OFFSET = 1;

  private Bauernschach game;
  private PrintMode printMode;

  /**
   * Read and process input until the quit command has been entered.
   *
   * @param args Command line arguments.
   * @throws IOException Error reading from stdin.
   */
  public static void main(String[] args) throws IOException {
    final Shell shell = new Shell();
    shell.run();
  }

  /**
   * Run the Bauernschach shell. Shows prompt 'BS> ', takes commands from the user and executes
   * them.
   */
  public void run() throws IOException {
    BufferedReader in =
        new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
    boolean quit = false;

    while (!quit) {
      System.out.print(PROMPT);

      String input = in.readLine();
      if (input == null) {
        break;
      }

      // remove all empty tokens
      String[] tokens =
          Arrays.stream(input.split("\\s+")).filter(e -> e.length() > 0).toArray(String[]::new);

      if (tokens.length == 0) {
        displayError(NO_COMMAND_MESSAGE);
        continue;
      }

      String command = tokens[0].toUpperCase();
      String[] arguments = Arrays.copyOfRange(tokens, 1, tokens.length);

      // Consider using enums instead (Effective Java Item 34)
      switch (command) {
        case PRINT_COMMAND:
          {
            handlePrintCommand(arguments);
            break;
          }
        case NEWGAME_COMMAND:
          {
            handleNewGameCommand(arguments);
            break;
          }
        case SELECT_COMMAND:
          {
            handleSelectCommand(arguments);
            break;
          }
        case DESELECT_COMMAND:
          {
            handleDeselectCommand(arguments);
            break;
          }
        case MOVE_COMMAND:
          {
            handleMoveCommand(arguments);
            break;
          }
        case PASS_COMMAND:
          {
            handlePassCommand(arguments);
            break;
          }
        case QUIT_COMMAND:
          {
            if (arguments.length != 0) {
              displayError(INVALID_ARGUMENTS_MESSAGE);
              continue;
            }
            quit = true;
            break;
          }
        case HELP_COMMAND:
          {
            if (arguments.length != 0) {
              displayError(INVALID_ARGUMENTS_MESSAGE);
              continue;
            }
            System.out.println(HELP_MESSAGE);
            break;
          }
        default:
          {
            displayError(INVALID_COMMAND_MESSAGE);
          }
      }
    }
  }

  private void handlePrintCommand(String[] arguments) {
    if (arguments.length != 0) {
      displayError(INVALID_ARGUMENTS_MESSAGE);
      return;
    }

    if (!isGameRunning()) {
      displayError(NO_ACTIVE_GAME_MESSAGE);
      return;
    }

    printCurrentChessBoard();
  }

  private void handleNewGameCommand(String[] arguments) {
    if (arguments.length != 0) {
      displayError(INVALID_ARGUMENTS_MESSAGE);
      return;
    }

    game = new Bauernschach();
    printMode = PrintMode.PIECE_SELECT;
    printCurrentChessBoard();
  }

  private void handleSelectCommand(String[] arguments) {
    if (!isGameRunning()) {
      displayError(NO_ACTIVE_GAME_MESSAGE);
      return;
    }

    if (arguments.length != 1) {
      displayError(INVALID_ARGUMENTS_MESSAGE);
      return;
    }

    int id = -1;
    try {
      id = Integer.parseInt(arguments[0]);
    } catch (NumberFormatException e) {
      displayError(INVALID_INPUT_MESSAGE);
      return;
    }

    OperationStatus status = game.selectPieceById(id);
    if (status == OperationStatus.SUCCESS) {
      printMode = PrintMode.MOVE_SELECT;
      printCurrentChessBoard();
    } else {
      displayError(NOT_POSSIBLE_MESSAGE);
    }
  }

  private void handleDeselectCommand(String[] arguments) {
    if (!isGameRunning()) {
      displayError(NO_ACTIVE_GAME_MESSAGE);
      return;
    }

    if (arguments.length != 0) {
      displayError(INVALID_ARGUMENTS_MESSAGE);
      return;
    }

    OperationStatus status = game.deselectPiece();
    if (status == OperationStatus.SUCCESS) {
      printMode = PrintMode.PIECE_SELECT;
      printCurrentChessBoard();
    } else {
      displayError(NOT_POSSIBLE_MESSAGE);
    }
  }

  private void handleMoveCommand(String[] arguments) {
    if (!isGameRunning()) {
      displayError(NO_ACTIVE_GAME_MESSAGE);
      return;
    }

    if (arguments.length != 1) {
      displayError(INVALID_ARGUMENTS_MESSAGE);
      return;
    }

    int id = -1;
    try {
      id = Integer.parseInt(arguments[0]);
    } catch (NumberFormatException e) {
      displayError(INVALID_INPUT_MESSAGE);
      return;
    }

    OperationStatus operationStatus = game.move(id);
    if (operationStatus == OperationStatus.FAIL) {
      displayError(NOT_POSSIBLE_MESSAGE);
      return;
    }

    if (game.getGameState().isGameRunning()) {
      printMode = PrintMode.PIECE_SELECT;
    } else {
      printMode = PrintMode.PLAIN;
    }
    printCurrentChessBoard();

    if (!game.getGameState().isGameRunning()) {
      game = null;
    }
  }

  private void handlePassCommand(String[] arguments) {
    if (!isGameRunning()) {
      displayError(NO_ACTIVE_GAME_MESSAGE);
      return;
    }

    if (arguments.length != 0) {
      displayError(INVALID_ARGUMENTS_MESSAGE);
      return;
    }

    game.pass();
    printMode = PrintMode.PIECE_SELECT;
    printCurrentChessBoard();
  }

  private void printCurrentChessBoard() {
    assert isGameRunning();

    final GameState gameState = game.getGameState();
    final GameState.GameStatus gameStatus = gameState.getStatus();
    final ChessBoard chessBoard = gameState.getChessBoard();

    final int numRows = chessBoard.getNumRows();
    final int numColumns = chessBoard.getNumColumns();

    // print column index (1~8)
    StringBuilder line = new StringBuilder("  ");
    for (int colNum = 0; colNum < numColumns; ++colNum) {
      line.append(" ").append(colNum + COLUMN_DISPLAY_OFFSET);
    }
    System.out.println(line);

    // print each row with row index (A~Z)
    for (int rowNum = 0; rowNum < numRows; ++rowNum) {
      line = new StringBuilder(" ");
      line.append((char) (rowNum + ROW_START_CHAR));
      for (int colNum = 0; colNum < numColumns; ++colNum) {
        Coordinate coord = Coordinate.of(rowNum, colNum);
        line.append(" ");
        line.append(chessPieceToString(coord, gameState));
      }
      System.out.println(line);
    }

    if (gameStatus == GameState.GameStatus.ONGOING) {
      System.out.println("Current round: " + gameState.getCurrentRound());
    } else if (gameStatus == GameState.GameStatus.WHITE_WON) {
      System.out.println("Player WHITE wins!");
    } else if (gameStatus == GameState.GameStatus.BLACK_WON) {
      System.out.println("Player BLACK wins!");
    } else { // gameStatus == GameStatus.DRAW
      System.out.println("No possible move left. Draw!");
    }

    if (printMode == PrintMode.PIECE_SELECT) {
      System.out.println(SELECT_PIECE_MESSAGE);
    } else if (printMode == PrintMode.MOVE_SELECT) {
      System.out.println(SELECT_MOVE_MESSAGE);
    }
  }

  private String pieceColorToString(ChessPiece.Color color) {
    return (color == ChessPiece.Color.WHITE) ? "W" : "B";
  }

  private String chessPieceToString(Coordinate coord, GameState gameState) {
    ChessPiece piece = gameState.getChessBoard().getPieceAt(coord);
    if (printMode == PrintMode.PIECE_SELECT) {
      if (piece.isNone()) {
        return ".";
      } else if (piece.getColor() == gameState.getCurrentRound()) {
        if (piece.hasPossibleMoves()) {
          return String.valueOf(piece.getId());
        } else {
          return pieceColorToString(piece.getColor());
        }
      } else { // piece.getColor() == currentRound
        return pieceColorToString(piece.getColor());
      }
    } else if (printMode == PrintMode.MOVE_SELECT) {
      if (piece.equals(gameState.getSelectedPiece())) {
        return "*";
      } else if (gameState.getSelectedPiece().getPossibleMoveCoordinates().contains(coord)) {
        final int i = gameState.getSelectedPiece().getPossibleMoveCoordinates().indexOf(coord);
        return String.valueOf(i);
      } else if (!piece.isNone()) {
        return pieceColorToString(piece.getColor());
      } else {
        return ".";
      }
    } else { // printMode == PrintMode.PLAIN
      if (piece.isNone()) {
        return ".";
      } else {
        return pieceColorToString(piece.getColor());
      }
    }
  }

  private void displayError(String message) {
    System.out.println("Error! " + message);
  }

  private boolean isGameRunning() {
    return game != null;
  }
}
