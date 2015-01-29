package algorithm.parsing.lr;

import algorithm.parsing.lr.machine.Machine;
import junit.framework.Assert;
import lexer.Lexer;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import sun.reflect.generics.visitor.Visitor;
import token.CompilationUnit;
import token.Token;
import visitor.GenericCheckVisitor;
import visitor.VisitorException;

import java.io.*;
import java.util.ArrayList;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
  public void testASTInput1() throws IOException, Lexer.LexerException, Machine.MachineException, VisitorException {
    testASTConstruction("src/test/resources/ast_input1", "PositiveTest");
  }

  @Test
  public void testASTInput2() throws IOException, Lexer.LexerException, Machine.MachineException, VisitorException {
    testASTConstruction("src/test/resources/ast_input2", "A");
  }

  @Test
  public void testValidJoosSpecification() throws IOException, Lexer.LexerException, Machine.MachineException, VisitorException {
    File files = new File("src/test/resources/JoosSpecificationTests/valid/");

    for(File file : files.listFiles()) {
      try {
        algm.reset();
        testASTConstruction(file.getAbsolutePath());
      } catch (IOException e) {
        e.printStackTrace();
      } catch (Lexer.LexerException | Machine.MachineException | VisitorException e) {
        e.printStackTrace();
        System.err.println("Exception on file: " + file.getAbsolutePath());
      }
    }
  }

  @Test
  public void testInvalidJoosSpecification() throws IOException, Lexer.LexerException, Machine.MachineException, VisitorException {
    File files = new File("src/test/resources/JoosSpecificationTests/invalid/");

    for(File file : files.listFiles()) {
      try {
        algm.reset();
        testASTConstruction(file.getAbsolutePath());
        assertTrue("Test " + file.getName() + " must fail due to invalid spec.", false);
      } catch (Lexer.LexerException | Machine.MachineException | VisitorException e) {
      }
    }
  }

  private void testASTConstruction(String inputFile) throws IOException, Lexer.LexerException, Machine.MachineException, VisitorException {
    testASTConstruction(inputFile, new File(inputFile).getName().replaceFirst("[.][^.]+$", ""));

  }

  private void testASTConstruction(String inputFile, String className) throws IOException, Lexer.LexerException, Machine.MachineException, VisitorException {
    ArrayList<Token> tokens = lexer.parse(new InputStreamReader(new FileInputStream(inputFile), "US-ASCII"));
    CompilationUnit unit = algm.constructAST(tokens);
    assertFalse(unit == null);
    unit.accept(new GenericCheckVisitor(className));
  }
}
