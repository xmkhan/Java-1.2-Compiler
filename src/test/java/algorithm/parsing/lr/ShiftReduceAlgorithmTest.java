package algorithm.parsing.lr;

import algorithm.parsing.lr.machine.Machine;
import junit.framework.Assert;
import lexer.Lexer;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import token.CompilationUnit;
import token.Token;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import static org.junit.Assert.assertFalse;
/**
 * Tests the basic functionality of the Shift-Reduce algorithm implementation
 */
public class ShiftReduceAlgorithmTest {
  private Lexer lexer;
  private ShiftReduceAlgorithm algm;

  @Before
  public void setUp() throws FileNotFoundException, IOException, Machine.MachineException {
    lexer =  new Lexer();
    algm = new ShiftReduceAlgorithm(new InputStreamReader(new FileInputStream(ShiftReduceAlgorithm.DEFAULT_LR1_FILE)));
  }

  @Test
  @Ignore
  public void testASTInput1() throws IOException, Lexer.LexerException, Machine.MachineException {
    testASTConstruction("src/test/resources/ast_input1");
  }

  @Test
  public void testASTInput2() throws IOException, Lexer.LexerException, Machine.MachineException {
    testASTConstruction("src/test/resources/ast_input2");
  }

  private void testASTConstruction(String inputFile) throws IOException, Lexer.LexerException, Machine.MachineException {
    ArrayList<Token> tokens = lexer.parse(new InputStreamReader(new FileInputStream(inputFile)));
    CompilationUnit unit = algm.constructAST(tokens);
    assertFalse(unit == null);

  }
}
