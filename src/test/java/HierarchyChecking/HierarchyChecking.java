package HierarchyChecking;


import algorithm.parsing.lr.ShiftReduceAlgorithm;
import exception.LexerException;
import exception.MachineException;
import exception.TypeHierarchyException;
import exception.VisitorException;
import lexer.Lexer;
import org.junit.Before;
import org.junit.Test;
import token.CompilationUnit;
import token.Token;
import type.hierarchy.ClassHierarchy;
import visitor.GenericCheckVisitor;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class HierarchyChecking {
  private Lexer lexer;
  private ShiftReduceAlgorithm algm;
  private File testsRoot = new File("src/test/resources/HierarchyChecking/Tests");
  private File classesAndInterfacesRoot = new File("src/test/resources/HierarchyChecking/ClassesAndInterfaces");

  @Before
  public void setUp() throws FileNotFoundException, IOException, MachineException {
    lexer = new Lexer();
    algm = new ShiftReduceAlgorithm(new InputStreamReader(new FileInputStream(ShiftReduceAlgorithm.DEFAULT_LR1_FILE)));
  }

  @Test
  public void test() throws IOException, LexerException, MachineException, VisitorException, TypeHierarchyException {
    testASTConstruction("src/test/resources/ast_input1", "PositiveTest");
  }

  @Test
  public void testASTInput2() throws IOException, LexerException, MachineException, VisitorException, TypeHierarchyException {
    testASTConstruction("src/test/resources/ast_input2", "A");
  }

  public void testClassClauseHierarchyChecking(ArrayList<String> fileNames) throws IOException, LexerException, MachineException, VisitorException, TypeHierarchyException {
    Queue folders = new LinkedList();
    folders.add(testsRoot);

    while (!folders.isEmpty()) {
      for (File file : ((File) folders.poll()).listFiles()) {
        if (file.isFile()) {
          try {
            algm.reset();
            lexer.resetDFAs();
            testASTConstruction(file.getAbsolutePath());
          } catch (IOException e) {
            e.printStackTrace();
          } catch (LexerException e) {
            e.printStackTrace();
            System.err.println("Exception on file: " + file.getAbsolutePath());
          } catch (MachineException e) {
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
  public void testInvalidJoosSpecification() throws IOException, LexerException, MachineException, VisitorException, TypeHierarchyException {
    File files = new File("src/test/resources/JoosSpecificationTests/invalid/");

    for (File file : files.listFiles()) {
      try {
        algm.reset();
        lexer.resetDFAs();
        testASTConstruction(file.getAbsolutePath());
        assertTrue("Test " + file.getName() + " must fail due to invalid spec.", false);
      } catch (LexerException e) {
      } catch (MachineException e) {
      } catch (VisitorException e) {
      }
    }
  }

  private void testASTConstruction(String inputFile) throws IOException, LexerException, MachineException, VisitorException, TypeHierarchyException {
    testASTConstruction(inputFile, new File(inputFile).getName().replaceFirst("[.][^.]+$", ""));

  }

  private void testASTConstruction(String inputFile, String className) throws IOException, LexerException, MachineException, VisitorException, TypeHierarchyException {
    ArrayList<Token> tokens = lexer.parse(new InputStreamReader(new FileInputStream(inputFile), "US-ASCII"));
    CompilationUnit unit = algm.constructAST(tokens);
    assertFalse(unit == null);
    unit.accept(new GenericCheckVisitor(className));

    List<CompilationUnit> compilationUnits = new ArrayList<CompilationUnit>();
    compilationUnits.add(unit);
    ClassHierarchy classHierarchy = new ClassHierarchy();
    classHierarchy.processCompilationUnits(compilationUnits);
  }
}
