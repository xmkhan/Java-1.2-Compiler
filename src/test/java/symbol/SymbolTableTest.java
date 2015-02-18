package symbol;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests the basic functionality of the symbol table w.r.t scopes.
 */
public class SymbolTableTest {
  private SymbolTable table;

  @Before
  public void setUp() {
    table = new SymbolTable();
  }

  @Test
  public void testSimple() {
    table.newScope();

  }

}
