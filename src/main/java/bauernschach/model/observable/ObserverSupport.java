package bauernschach.model.observable;

import bauernschach.model.GameState;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

/** Class representing an observer support with subscribing / unsubscribing methods. */
public final class ObserverSupport implements Observable {

  private ConcurrentLinkedQueue<Observer> observers = new ConcurrentLinkedQueue<>();

  @Override
  public void subscribe(Observer obsv) {
    if (observers.contains(obsv)) {
      throw new AssertionError("Observer " + obsv + " already part of observers");
    }
    observers.add(obsv);
  }

  @Override
  public void unsubscribe(Observer obsv) {
    observers.remove(obsv);
    if (observers.contains(obsv)) {
      throw new AssertionError("Observer " + obsv + " still part of observers");
    }
  }

  @Override
  public void notifyAboutState(GameState state) {
    notifyAll(o -> o.updateState(state));
  }

  private void notifyAll(Consumer<Observer> toCall) {
    for (Observer o : observers) {
      toCall.accept(o);
    }
  }
}
