import algorithm.parsing.lr.ShiftReduceAlgorithm;
import lexer.Lexer;
import token.CompilationUnit;
import token.Token;
import visitor.GenericCheckVisitor;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Main executable for CS444
 */
public class Main {

  public static void main(String[] args) {
    try {
      InputStreamReader reader = new InputStreamReader(new FileInputStream(args[0]), "US-ASCII");
      InputStreamReader lr1Reader = new InputStreamReader(new FileInputStream(ShiftReduceAlgorithm.DEFAULT_LR1_FILE));

      Lexer lexer = new Lexer();
      ShiftReduceAlgorithm shiftReduceAlgorithm = new ShiftReduceAlgorithm(lr1Reader);

      ArrayList<Token> tokens = lexer.parse(reader);
      CompilationUnit compilationUnit = shiftReduceAlgorithm.constructAST(tokens);

      compilationUnit.accept(new GenericCheckVisitor(new File(args[0]).getName()));

    } catch (Exception e) {
      System.exit(42);
    }
    System.exit(0);
  }
}
