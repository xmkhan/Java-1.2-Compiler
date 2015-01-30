package algorithm.parsing.lr;

import algorithm.parsing.lr.machine.Machine;
import lexer.Lexer;
import org.junit.Before;
import org.junit.Test;
import token.CompilationUnit;
import token.Token;
import visitor.GenericCheckVisitor;
import visitor.VisitorException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

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
    lexer = new Lexer();
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
    File root = new File("src/test/resources/JoosSpecificationTests/valid");
    Queue<File> folders = new LinkedList<>();
    folders.add(root);

    while(!folders.isEmpty()) {
      for (File file : folders.poll().listFiles()) {
        if (file.isFile()) {
          try {
            algm.reset();
            lexer.resetDFAs();
            testASTConstruction(file.getAbsolutePath());
          } catch (IOException e) {
            e.printStackTrace();
          } catch (Lexer.LexerException e) {
            e.printStackTrace();
            System.err.println("Exception on file: " + file.getAbsolutePath());
          } catch (Machine.MachineException e) {
            e.printStackTrace();
            System.err.println("Exception on file: " + file.getAbsolutePath());
          } catch (VisitorException e) {
            e.printStackTrace();
            System.err.println("Exception on file: " + file.getAbsolutePath());
          }
        } else if (file.isDirectory()) {
          folders.add(file);
        }
      }


    }
  }

  @Test
  public void testInvalidJoosSpecification() throws IOException, Lexer.LexerException, Machine.MachineException, VisitorException {
    File files = new File("src/test/resources/JoosSpecificationTests/invalid/");

    for (File file : files.listFiles()) {
      try {
        algm.reset();
        lexer.resetDFAs();
        testASTConstruction(file.getAbsolutePath());
        assertTrue("Test " + file.getName() + " must fail due to invalid spec.", false);
      } catch (Lexer.LexerException e) {
      } catch (Machine.MachineException e) {
      } catch (VisitorException e) {
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
