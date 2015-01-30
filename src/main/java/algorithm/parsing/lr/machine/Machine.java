package algorithm.parsing.lr.machine;

import algorithm.base.Pair;
import token.CompilationUnit;
import token.Token;
import token.TokenType;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
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
    machineStates = new ArrayList<MachineState>(size);
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
    // This is looped so that after each reduction, we imply that token is incoming (nested reduction).
    while (states.peek().getTransition(token) != null &&
        states.peek().getTransition(token).getFirst() == MachineState.Action.REDUCE) {
      performReduction(states.peek().getTransition(token));
    }
    if (token.getTokenType() != TokenType.EOF) {
      performShift(token, states.peek().getTransition(token));
    }
  }


  private void performReduction(Pair<MachineState.Action, Integer> actionPair) throws MachineException {
    while (actionPair != null && actionPair.getFirst() == MachineState.Action.REDUCE) {
      // Get the reduction production rule.
      List<String> productionRule = productionRules.get(actionPair.getSecond());

      // Check in reverse because a stack is an reverse insertion order.
      List<Token> rhs = new ArrayList<Token>();
      for (int i = productionRule.size() - 1; i > 0; --i) {
        if (!tokens.peek().getTokenType().toString().equals(productionRule.get(i))) {
          throw new MachineException("Parse error on token: " + tokens.peek().getTokenType().toString() +
              " vs. " + productionRule.get(i));
        }
        rhs.add(tokens.pop());
        states.pop();
      }
      Collections.reverse(rhs);
      // Use the 0th element as the reduction class.
      Token reducedToken;
      try {
        Class<? extends Token> lhsClass = Class.forName("token." + productionRule.get(0)).asSubclass(Token.class);
        reducedToken = lhsClass.getConstructor(ArrayList.class).newInstance(rhs);
      } catch (InstantiationException e) {
        throw new MachineException(e.getMessage() + " token: " + productionRule.get(0));
      } catch (IllegalAccessException e) {
        throw new MachineException(e.getMessage() + " token: " + productionRule.get(0));
      } catch (ClassNotFoundException e) {
        throw new MachineException(e.getMessage() + " token: " + productionRule.get(0));
      } catch (NoSuchMethodException e) {
        throw new MachineException(e.getMessage() + " token: " + productionRule.get(0));
      } catch (InvocationTargetException e) {
        throw new MachineException(e.getMessage() + " token: " + productionRule.get(0));
      }
      // Finally perform the transition using the reduced token.
      actionPair = states.peek().getTransition(reducedToken);
      tokens.push(reducedToken);
      states.push(machineStates.get(actionPair.getSecond()));
    }
  }

  private void performShift(Token token, Pair<MachineState.Action, Integer> actionPair) throws MachineException {
    if (actionPair == null || actionPair.getFirst() != MachineState.Action.SHIFT) {
      throw new MachineException("Expected a shift on token: " + token.getTokenType().toString());
    }
    tokens.push(token);
    states.push(machineStates.get(actionPair.getSecond()));
  }

  public CompilationUnit getResult() throws MachineException {
    if (tokens.size() != 1 || !(tokens.peek() instanceof CompilationUnit)) {
      throw new MachineException("List of tokens did not correctly parse into a CompilationUnit");
    }
    return (CompilationUnit) tokens.pop();
  }

  public void reset() {
    tokens = new Stack<Token>();
    states = new Stack<MachineState>();
    states.push(machineStates.get(0));
  }

  public static class MachineException extends Exception {
    public MachineException(String message) {
      super(message);
    }
  }

}
