package lr.machine;

import algorithm.base.Pair;
import token.Token;

import java.util.HashMap;

/**
 * Represents a LR(1) machine state.
 */
public class MachineState {

  public enum Action { SHIFT, REDUCE }

  private int stateId = -1;

  private HashMap<Token, Pair<Action, MachineState>> transitions;

  public MachineState(int stateId) {
    this.stateId = stateId;
  }

  public void addTransition(Token token, Action action, MachineState state) {
    transitions.put(token, new Pair<>(action, state));
  }

  public Pair<Action, MachineState> getTransition(Token token) {
    return transitions.get(token);
  }

}
