import algorithm.parsing.lr.ShiftReduceAlgorithm;
import algorithm.parsing.lr.machine.Machine;
import lexer.Lexer;
import token.CompilationUnit;
import token.Token;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Main executable for CS444
 */
public class Main {

  public static void main(String[] args) {
    try {
      InputStreamReader reader = new InputStreamReader(new FileInputStream(args[1]), "US-ASCII");
      InputStreamReader cfgReader = new InputStreamReader(new FileInputStream(ShiftReduceAlgorithm.DEFAULT_LR1_FILE));

      Lexer lexer = new Lexer();
      ShiftReduceAlgorithm shiftReduceAlgorithm = new ShiftReduceAlgorithm(cfgReader);

      ArrayList<Token> tokens = lexer.parse(reader);
      CompilationUnit compilationUnit = shiftReduceAlgorithm.constructAST(tokens);
    } catch (IOException | Lexer.LexerException | Machine.MachineException e) {
      e.printStackTrace();
    }
  }
}
