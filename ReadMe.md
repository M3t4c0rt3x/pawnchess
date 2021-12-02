# GUI: Bauernschach Hotseat (2 Spieler lokal)

## Shell

### How to Run
To start the Shell:

```
./gradlew runShell --console plain
```

### Command Usage
```
Commands:
- NEWGAME: create a new Bauernschach game
- PRINT: print the current chess board
- SELECT <int chess_id>: select the chess piece by ID (number shown on board tiles)
- DESELECT: deselect the selected chess piece
- MOVE <int move_id>: move the selected chess according to the chosen move (number shown on board tiles)
- PASS: pass the current round
- QUIT: quit the shell
- HELP: print the help message
```


## GUI

### How to Run

To start the GUI (Main):
```
./gradlew run
```