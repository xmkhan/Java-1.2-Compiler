package symbol;

import org.junit.Before;
import org.junit.Test;
import token.Declaration;
import token.Token;
import token.TokenType;

import java.util.ArrayList;
import java.util.List;

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

  @Test
  public void testPrefixDecls() {
    table.newScope();
    Declaration decl1 = new Declaration("Comparable", TokenType.ClassType, new ArrayList<Token>());
    Declaration decl2 = new Declaration("Object", TokenType.ClassType, new ArrayList<Token>());
    Declaration decl3 = new Declaration("String", TokenType.ClassType, new ArrayList<Token>());
    Declaration decl4 = new Declaration("TEST", TokenType.ClassType, new ArrayList<Token>());
    table.addDecl("java.lang.Comparable", decl1);
    table.addDecl("java.lang.Object", decl2);
    table.addDecl("java.lang.String", decl3);
    table.addDecl("java.TEST", decl4);

    List<Token> matches = table.findWithPrefix("java.lang");
    assertEquals(3, matches.size());
    assertTrue(matches.contains(decl1));
    assertTrue(matches.contains(decl2));
    assertTrue(matches.contains(decl3));
    assertFalse(matches.contains(decl4));
  }

}
