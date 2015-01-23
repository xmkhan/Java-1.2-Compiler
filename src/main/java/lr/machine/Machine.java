package lr.machine;

import algorithm.base.Pair;
import token.Token;

import java.util.ArrayList;
import java.util.Stack;

/**
 * Represents an LR(1) machine.
 */
public class Machine {
  private ArrayList<MachineState> machineStates;
  private Stack<Token> tokens;
  private MachineState curState;


  public Machine(int size) {
    machineStates = new ArrayList<>(size);
    tokens = new Stack<>();
    for (int i = 0; i < size; ++i) {
      machineStates.add(new MachineState(i));
    }
    curState = machineStates.get(0);
  }

  public Machine addTransition(int state0, Token token, MachineState.Action action, int state1) {
    machineStates.get(state0).addTransition(token, action, machineStates.get(state1));
    return this;
  }

  public Machine applyAction(Token token) {
    Pair<MachineState.Action, MachineState> actionPair = machineStates
  }

}
