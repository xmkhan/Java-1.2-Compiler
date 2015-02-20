package HierarchyChecking;

import algorithm.parsing.lr.ShiftReduceAlgorithm;
import exception.*;
import lexer.Lexer;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import token.CompilationUnit;
import token.Token;
import type.hierarchy.HierarchyChecker;
import visitor.GenericCheckVisitor;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class HierarchyCheckerTest {
  private Lexer lexer;
  private ShiftReduceAlgorithm algm;
  private File classesAndInterfacesFolder = new File("src/test/resources/HierarchyChecking/ClassesAndInterfaces");

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Before
  public void setUp() throws FileNotFoundException, IOException, MachineException {
    lexer = new Lexer();
    algm = new ShiftReduceAlgorithm(new InputStreamReader(new FileInputStream(ShiftReduceAlgorithm.DEFAULT_LR1_FILE)));
  }

  @Test
  public void testValidHierarchy() throws CompilerException, IOException {
    executeTests("src/test/resources/HierarchyChecking/ValidTests", false);
  }

  @Test
  public void testTemp() throws CompilerException, IOException {
    executeTests("src/test/resources/HierarchyChecking/temp", false);
  }

  @Test
  public void testInvalidHierarchy() throws CompilerException, IOException {
    executeTests("src/test/resources/HierarchyChecking/InvalidTests", true);
  }

  private void executeTests(String testsFolder, boolean invalidTests) throws CompilerException, IOException {
    Queue folders = new LinkedList();
    folders.add(new File(testsFolder));

    while (!folders.isEmpty()) {
      for (File file : ((File) folders.poll()).listFiles()) {
        if (file.isFile()) {
          try {
            if(invalidTests) {
              // Expects invalid tests to throw a TypeHierarchyException
              exception.expect(TypeHierarchyException.class);
              testHierarchyChecking(file.getAbsolutePath());
            } else {
              // Exceptions will cause the test to fail
              testHierarchyChecking(file.getAbsolutePath());
            }
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

  private void testHierarchyChecking(String inputFile) throws IOException, CompilerException {
    BufferedReader br = new BufferedReader(new FileReader(inputFile));
    String line;
    List<String> classNames = new ArrayList<String>();
    while ((line = br.readLine()) != null) {
      classNames.add(line);
    }
    br.close();
    testHierarchyChecking(classNames);
  }

  private void testHierarchyChecking(List<String> classNames) throws IOException, CompilerException {
    List<CompilationUnit> compilationUnits = new ArrayList<CompilationUnit>(classNames.size());
    // 1. Phase 1: Construct the AST per CompilationUnit (per class), and do basic static checks.
    for (String className : classNames) {
      algm.reset();
      lexer.resetDFAs();
      String path = classesAndInterfacesFolder + "/" + className;
      InputStreamReader reader = new InputStreamReader(new FileInputStream(path), "US-ASCII");
      ArrayList<Token> tokens = lexer.parse(reader);
      CompilationUnit compilationUnit = algm.constructAST(tokens);
      compilationUnit.accept(new GenericCheckVisitor(new File(path).getName()));
      compilationUnits.add(compilationUnit);
    }

    HierarchyChecker hierarchyChecker = new HierarchyChecker();
    hierarchyChecker.verifyClassAndInterfaceHierarchy(compilationUnits);
  }
}
