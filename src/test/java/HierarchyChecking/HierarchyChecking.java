package HierarchyChecking;

import algorithm.parsing.lr.ShiftReduceAlgorithm;
import exception.LexerException;
import exception.MachineException;
import exception.TypeHierarchyException;
import exception.VisitorException;
import lexer.Lexer;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import token.CompilationUnit;
import token.Token;
import type.hierarchy.ClassHierarchy;
import visitor.GenericCheckVisitor;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class HierarchyChecking {
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
  public void testNew() throws LexerException, TypeHierarchyException, VisitorException, MachineException, IOException {
    executeTests("src/test/resources/HierarchyChecking/NewTests", false);
  }

  @Test
  public void testValidHierarchy() throws LexerException, TypeHierarchyException, VisitorException, MachineException, IOException {
    executeTests("src/test/resources/HierarchyChecking/ValidTests", false);
  }

  @Test
  public void testInvalidHierarchy() throws LexerException, TypeHierarchyException, VisitorException, MachineException, IOException {
    executeTests("src/test/resources/HierarchyChecking/InvalidTests", true);
  }

  private void executeTests(String testsFolder, boolean invalidTests) throws IOException, LexerException, MachineException, VisitorException, TypeHierarchyException {
    Queue folders = new LinkedList();
    folders.add(new File(testsFolder));

    while (!folders.isEmpty()) {
      for (File file : ((File) folders.poll()).listFiles()) {
        if (file.isFile()) {
          try {
            algm.reset();
            lexer.resetDFAs();

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

  private void testHierarchyChecking(String inputFile) throws IOException, LexerException, MachineException, VisitorException, TypeHierarchyException {
    BufferedReader br = new BufferedReader(new FileReader(inputFile));
    String line;
    List<String> classNames = new ArrayList<>();
    while ((line = br.readLine()) != null) {
      classNames.add(line);
    }
    br.close();
    testHierarchyChecking(classNames);
  }

  private void testHierarchyChecking(List<String> classNames) throws IOException, LexerException, MachineException, VisitorException, TypeHierarchyException {
    List<CompilationUnit> compilationUnits = new ArrayList<CompilationUnit>(classNames.size());
    // 1. Phase 1: Construct the AST per CompilationUnit (per class), and do basic static checks.
    for (String className : classNames) {
      String path = classesAndInterfacesFolder + "/" + className;
      InputStreamReader reader = new InputStreamReader(new FileInputStream(path), "US-ASCII");
      ArrayList<Token> tokens = lexer.parse(reader);
      CompilationUnit compilationUnit = algm.constructAST(tokens);
      compilationUnit.accept(new GenericCheckVisitor(new File(path).getName()));
      compilationUnits.add(compilationUnit);
      algm.reset();
      lexer.resetDFAs();
    }

    ClassHierarchy classHierarchy = new ClassHierarchy();
    classHierarchy.verifyClassHierarchy(compilationUnits);
  }
}
