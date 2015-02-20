package visitor;

import exception.CompilerException;
import exception.EnvironmentBuildingVisitorException;
import org.junit.Before;
import org.junit.Test;
import symbol.SymbolTable;
import token.CompilationUnit;
import util.CompilationUnitGenerator;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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

  @Test(expected = EnvironmentBuildingVisitorException.class)
  public void testSameFieldName() throws IOException, CompilerException {
    List<CompilationUnit> units = CompilationUnitGenerator.make(new String[] {
        "src/test/resources/environment_check/test2",
    });
    visitor.buildGlobalScope(units);
  }

  @Test(expected = EnvironmentBuildingVisitorException.class)
  public void testOverlappingLocalVar() throws IOException, CompilerException {
    List<CompilationUnit> units = CompilationUnitGenerator.make(new String[] {
        "src/test/resources/environment_check/test3",
    });
    visitor.buildGlobalScope(units);
  }

  @Test(expected = EnvironmentBuildingVisitorException.class)
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

  @Test(expected = EnvironmentBuildingVisitorException.class)
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

  @Test
  public void testStdlib() throws IOException, CompilerException {
    List<CompilationUnit> units = CompilationUnitGenerator.make(new String[] {
        "src/test/resources/stdlib/java/io/PrintStream.java",
        "src/test/resources/stdlib/java/io/Serializable.java",
        "src/test/resources/stdlib/java/lang/Boolean.java",
        "src/test/resources/stdlib/java/lang/Byte.java",
        "src/test/resources/stdlib/java/lang/Character.java",
        "src/test/resources/stdlib/java/lang/Class.java",
        "src/test/resources/stdlib/java/lang/Cloneable.java",
        "src/test/resources/stdlib/java/lang/Integer.java",
        "src/test/resources/stdlib/java/lang/Number.java",
        "src/test/resources/stdlib/java/lang/Object.java",
        "src/test/resources/stdlib/java/lang/Short.java",
        "src/test/resources/stdlib/java/lang/String.java",
        "src/test/resources/stdlib/java/lang/System.java",
        "src/test/resources/stdlib/java/util/Arrays.java",
    });
    visitor.buildGlobalScope(units);
  }

  @Test
  public void testSymbolTable() throws IOException, CompilerException {
    List<CompilationUnit> units = CompilationUnitGenerator.make(new String[] {
        "src/test/resources/environment_check/symbol1",
    });
    visitor.buildGlobalScope(units);
    assertTrue(table.contains("symbols"));
    assertTrue(table.contains("symbols.symbol1"));
    assertTrue(table.contains("symbols.symbol1.a"));
    assertTrue(table.contains("symbols.symbol1.b"));
    assertTrue(table.contains("symbols.symbol1.c"));
    assertTrue(table.contains("symbols.symbol1.symbol1"));
    assertTrue(table.contains("symbols.symbol1.m1"));
    assertTrue(table.contains("symbols.symbol1.m2"));

    assertFalse(table.contains("symbols.symbol1.x"));
    assertFalse(table.contains("x"));

    assertEquals(1, table.findAll("symbols.symbol1.a").size());
    assertEquals(1, table.findAll("symbols.symbol1.b").size());
  }

}
