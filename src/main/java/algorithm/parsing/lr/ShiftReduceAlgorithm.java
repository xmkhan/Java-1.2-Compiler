package algorithm.parsing.lr;

import algorithm.parsing.lr.machine.Machine;
import algorithm.parsing.lr.machine.MachineState;
import token.CompilationUnit;
import token.Token;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This is a special instance of the Shift-reduce algorithm that reads the CS444 .LR1 file.
 */
public class ShiftReduceAlgorithm {
  public static String DEFAULT_LR1_FILE = "src/main/resources/output.lr1";
  private Machine machine;


  public ShiftReduceAlgorithm(InputStreamReader reader) throws IOException, Machine.MachineException {
    BufferedReader bufferedReader = new BufferedReader(reader);

    // 1. Number of terminals.
    Integer numTerminals = Integer.valueOf(bufferedReader.readLine());
    for (int i = 0; i < numTerminals; ++i) bufferedReader.readLine();

    // 2. Number of non-terminals
    Integer numNonterminals = Integer.valueOf(bufferedReader.readLine());
    for (int i = 0; i < numNonterminals; ++i) bufferedReader.readLine();

    // Ignore start state.
    bufferedReader.readLine();

    // 3. Production rules
    Integer numProductionRules = Integer.valueOf(bufferedReader.readLine());
    List<List<String>> productionRules = new ArrayList<List<String>>(numProductionRules);
    for (int i = 0; i < numProductionRules; ++i) {
      List<String> rule = new ArrayList<String>();
      rule.addAll(Arrays.asList(bufferedReader.readLine().split(" ")));
      productionRules.add(rule);
    }

    // 3. Num states, setup the machine.
    Integer numStates = Integer.valueOf(bufferedReader.readLine());
    machine = new Machine(numStates);
    machine.addProductionRule(productionRules);

    // 4. Reduce or Shift transitions
    Integer numTransitions = Integer.valueOf(bufferedReader.readLine());
    for (int i = 0; i < numTransitions; ++i) {
      String[] transition = bufferedReader.readLine().split(" ");
      MachineState.Action action = transition[2].equals("shift") ?
          MachineState.Action.SHIFT : MachineState.Action.REDUCE;
      machine.addTransition(Integer.valueOf(transition[0]), transition[1], action, Integer.valueOf(transition[3]));
    }
  }

  public CompilationUnit constructAST(ArrayList<Token> tokens) throws Machine.MachineException {
    for (Token token : tokens) {
      machine.applyAction(token);
    }
    return machine.getResult();
  }

  public void reset() {
    machine.reset();
  }

}
