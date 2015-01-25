package algorithm.parsing.lr.machine;

import algorithm.base.Pair;
import sun.tools.java.ClassNotFound;
import token.CompilationUnit;
import token.Token;
import token.TokenType;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

/**
 * Represents an LR(1) machine.
 */
public class Machine {
  private ArrayList<MachineState> machineStates;
  private List<List<String>> productionRules;
  private Stack<Token> tokens;
  private Stack<MachineState> states;


  public Machine(int size) {
    machineStates = new ArrayList<>(size);
    for (int i = 0; i < size; ++i) {
      machineStates.add(new MachineState(i));
    }
    reset();
  }

  public void addProductionRule(List<List<String>> productionRules) throws MachineException {
    this.productionRules = productionRules;
  }

  public void addTransition(int state0, String token, MachineState.Action action, int stateOrRule) {
    machineStates.get(state0).addTransition(token, action, stateOrRule);
  }

  public void applyAction(Token token) throws MachineException {
    Pair<MachineState.Action, Integer> actionPair = states.peek().getTransition(token);
    if (actionPair == null) {
      throw new MachineException("Invalid token, there is no transition state, token: " + token);
    }
    tokens.push(token);
    switch (actionPair.getFirst()) {
      case REDUCE:
        // Get the reduction production rule.
        List<String> productionRule = productionRules.get(actionPair.getSecond());

        // Check in reverse because a stack is an reverse insertion order.
        List<Token> rhs = new ArrayList<>();
        for (int i = productionRule.size(); i > 0; --i) {
          if (tokens.peek().getTokenType() != TokenType.getTokenType(productionRule.get(i))) {
            throw new MachineException("Parse error on token: " + token);
          }
          rhs.add(tokens.pop());
          states.pop();
        }
        // Use the 0th element as the reduction class.
        try {
          Class<? extends Token> lhsClass = Class.forName(productionRule.get(0)).asSubclass(Token.class);
          Token reducedToken = lhsClass.getConstructor(List.class).newInstance(rhs);
          tokens.push(reducedToken);
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException |
            NoSuchMethodException | InvocationTargetException e) {
          throw new MachineException(e.getMessage());
        }
        // Finally perform the transition using the reduced token.
        actionPair = states.peek().getTransition(token);
        if (actionPair == null || actionPair.getFirst() == MachineState.Action.REDUCE) {
          throw new MachineException("Invalid token, there is no transition state, token: " + token);
        }
        // Fall-through
      case SHIFT:
        states.push(machineStates.get(actionPair.getSecond()));
        break;
    }
  }

  public CompilationUnit getResult() throws MachineException {
    if (tokens.size() != 1 || !(tokens.peek() instanceof CompilationUnit)) {
      throw new MachineException("List of tokens did not correctly parse into a CompilationUnit");
    }
    return (CompilationUnit) tokens.pop();
  }

  public void reset() {
    tokens = new Stack<>();
    states = new Stack<>();
    states.push(machineStates.get(0));
  }

  public static class MachineException extends Exception {
    public MachineException(String message) {
      super(message);
    }
  }

}
