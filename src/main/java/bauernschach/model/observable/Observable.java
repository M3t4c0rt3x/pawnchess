package bauernschach.model.observable;

import bauernschach.model.GameState;

/** Interface representing an observable. */
public interface Observable {

  void subscribe(Observer obsv);

  void unsubscribe(Observer obsv);

  void notifyAboutState(GameState newState);
}
