package bauernschach.model.observable;

import bauernschach.model.GameState;

/** Interface representing an observer. */
public interface Observer {

  /** Notify this observer of an update in the game state. */
  void updateState(GameState state);
}
