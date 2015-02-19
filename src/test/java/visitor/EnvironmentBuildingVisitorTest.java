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
        "src/test/resources/environment_check/test1",
    });
    visitor.buildGlobalScope(units);
  }

  @Test(expected = EnvironmentBuildingException.class)
  public void testSameFieldName() throws IOException, CompilerException {
    List<CompilationUnit> units = CompilationUnitGenerator.make(new String[] {
        "src/test/resources/environment_check/test2",
    });
    visitor.buildGlobalScope(units);
  }

  @Test(expected = EnvironmentBuildingException.class)
  public void testOverlappingLocalVar() throws IOException, CompilerException {
    List<CompilationUnit> units = CompilationUnitGenerator.make(new String[] {
        "src/test/resources/environment_check/test3",
    });
    visitor.buildGlobalScope(units);
  }

  @Test(expected = EnvironmentBuildingException.class)
  public void testSameClassName() throws IOException, CompilerException {
    List<CompilationUnit> units = CompilationUnitGenerator.make(new String[] {
        "src/test/resources/environment_check/test41",
        "src/test/resources/environment_check/test42",
    });
    visitor.buildGlobalScope(units);
  }

  @Test
  public void testSameMethodNameAsConstructor() throws IOException, CompilerException {
    List<CompilationUnit> units = CompilationUnitGenerator.make(new String[] {
        "src/test/resources/environment_check/test5",
    });
    visitor.buildGlobalScope(units);
  }

  @Test
  public void testDifferentPackageSameName() throws IOException, CompilerException {
    List<CompilationUnit> units = CompilationUnitGenerator.make(new String[] {
        "src/test/resources/environment_check/test61",
        "src/test/resources/environment_check/test62",
    });
    visitor.buildGlobalScope(units);
  }

  @Test
  public void testValidLocalVarScoping() throws IOException, CompilerException {
    List<CompilationUnit> units = CompilationUnitGenerator.make(new String[] {
        "src/test/resources/environment_check/J1_localvariablescope",
    });
    visitor.buildGlobalScope(units);
  }

  @Test
  public void testValidLocalVarConflict() throws IOException, CompilerException {
    List<CompilationUnit> units = CompilationUnitGenerator.make(new String[] {
        "src/test/resources/environment_check/J1_local_duplicate",
    });
    visitor.buildGlobalScope(units);
  }

  @Test(expected = EnvironmentBuildingException.class)
  public void testParameterForloopOverlap() throws IOException, CompilerException {
    List<CompilationUnit> units = CompilationUnitGenerator.make(new String[] {
        "src/test/resources/environment_check/Je_2_Parameter_OverlappingWithLocalInLoop",
    });
    visitor.buildGlobalScope(units);
  }

  @Test
  public void testStandard() throws IOException, CompilerException {
    List<CompilationUnit> units = CompilationUnitGenerator.make(new String[] {
        "src/test/resources/environment_check/J1_01",
    });
    visitor.buildGlobalScope(units);
  }

}
