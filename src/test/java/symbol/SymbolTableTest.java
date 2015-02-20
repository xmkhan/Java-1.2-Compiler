package symbol;

import org.junit.Before;
import org.junit.Test;
import token.Declaration;
import token.Token;
import token.TokenType;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

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
    table.addDecl("test", new Declaration("test", TokenType.ClassType, new ArrayList<Token>()));
    assertTrue(table.contains("test"));
    table.deleteScope();
    assertFalse(table.contains("test"));
  }

  @Test
  public void testMultipleScopeDecls() {
    table.newScope();
    table.addDecl("test", new Declaration("test", TokenType.ClassType, new ArrayList<Token>()));
    table.newScope();
    table.addDecl("test", new Declaration("test", TokenType.ClassType, new ArrayList<Token>()));
    assertEquals(2, table.findAll("test").size());
    table.deleteScope();
    assertTrue(table.contains("test"));
    table.deleteScope();
    assertFalse(table.contains("test"));
  }

}
