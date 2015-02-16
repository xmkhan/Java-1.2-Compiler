package algorithm.parsing.lr.machine;

import algorithm.base.Pair;
import token.Token;

import java.util.HashMap;

/**
 * Represents a LR(1) machine state information.
 */
public class MachineState {

  public enum Action {SHIFT, REDUCE}

  private int stateId = -1;
  private HashMap<String, Pair<Action, Integer>> transitions;

  public MachineState(int stateId) {
    this.stateId = stateId;
    transitions = new HashMap<String, Pair<Action, Integer>>();
  }

  public int getStateId() {
    return stateId;
  }

  public void addTransition(String token, Action action, Integer stateOrRule) {
    transitions.put(token, new Pair<Action, Integer>(action, stateOrRule));
  }

  public Pair<Action, Integer> getTransition(Token token) {
    return transitions.get(token.getTokenType().toString());
  }

}
