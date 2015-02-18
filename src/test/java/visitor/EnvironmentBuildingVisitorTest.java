package visitor;

import org.junit.Before;
import org.junit.Test;
import symbol.SymbolTable;

/**
 * Tests the basic functionality of EnvironmentBuildingVisitor
 */
public class EnvironmentBuildingVisitorTest {
  private EnvironmentBuildingVisitor visitor;
  private SymbolTable table;

  @Before
  public void setUp() {
    table = new SymbolTable();
    visitor = new EnvironmentBuildingVisitor(table);
  }

  @Test
  public void testSimple() {

  }
}
