package visitor;

import exception.CompilerException;
import exception.EnvironmentBuildingException;
import org.junit.Before;
import org.junit.Test;
import symbol.SymbolTable;
import token.CompilationUnit;
import util.CompilationUnitGenerator;

import java.io.IOException;
import java.util.List;

/**
 * Tests the basic functionality of EnvironmentBuildingVisitor
 */
public class EnvironmentBuildingVisitorTest {
  private SymbolTable table;
  private EnvironmentBuildingVisitor visitor;

  @Before
  public void setUp() {
    table = new SymbolTable();
    visitor = new EnvironmentBuildingVisitor(table);
  }

  @Test
  public void testSimple() throws IOException, CompilerException {
    List<CompilationUnit> units = CompilationUnitGenerator.make(new String[] {
        "src/test/resources/test1",
    });
    visitor.buildGlobalScope(units);
  }

  @Test(expected = EnvironmentBuildingException.class)
  public void testSameFieldName() throws IOException, CompilerException {
    List<CompilationUnit> units = CompilationUnitGenerator.make(new String[] {
        "src/test/resources/test2",
    });
    visitor.buildGlobalScope(units);
  }

  @Test(expected = EnvironmentBuildingException.class)
  public void testOverlappingLocalVar() throws IOException, CompilerException {
    List<CompilationUnit> units = CompilationUnitGenerator.make(new String[] {
        "src/test/resources/test3",
    });
    visitor.buildGlobalScope(units);
  }

  @Test(expected = EnvironmentBuildingException.class)
  public void testSameClassName() throws IOException, CompilerException {
    List<CompilationUnit> units = CompilationUnitGenerator.make(new String[] {
        "src/test/resources/test41",
        "src/test/resources/test42",
    });
    visitor.buildGlobalScope(units);
  }
}
