package dfa;

import org.junit.Before;
import org.junit.Test;
import token.Token;
import token.TokenType;

import static org.junit.Assert.assertEquals;

/**
 * Basic tests to test the Literal DFA.
 */
public class LiteralDFATest {
  private LiteralDFA dfa = new LiteralDFA();

  @Before
  public void setUp() {
    dfa.reset();
  }

  @Test
  public void testEmptySingleQuoteLiteral() {
    String literal = "''";

    for (char c : literal.toCharArray()) {
      dfa.consume(c);
    }
    dfa.consume('\n');

    Token result = dfa.getToken();
    assertEquals(result.getTokenType(), TokenType.STR_LITERAL);
    assertEquals(result.getLexeme(), "''");
  }

  @Test
  public void testEmptyDoubleQuoteLiteral() {
    String literal = "\"\"";

    for (char c : literal.toCharArray()) {
      dfa.consume(c);
    }
    dfa.consume('\n');

    Token result = dfa.getToken();
    assertEquals(result.getTokenType(), TokenType.STR_LITERAL);
    assertEquals(result.getLexeme(), "\"\"");
  }


  @Test
  public void testSingleQuoteInSingleQuotes() {
    String literal = "'\\''";

    for (char c : literal.toCharArray()) {
      dfa.consume(c);
    }
    dfa.consume('\n');

    Token result = dfa.getToken();
    assertEquals(result.getTokenType(), TokenType.STR_LITERAL);
    assertEquals(result.getLexeme(), "'\\''");
  }

  @Test
  public void testDoubleQuoteInDoubleQuotes() {
    String literal = "\"\\\"\"";

    for (char c : literal.toCharArray()) {
      dfa.consume(c);
    }
    dfa.consume('\n');

    Token result = dfa.getToken();
    assertEquals(result.getTokenType(), TokenType.STR_LITERAL);
    assertEquals(result.getLexeme(), "\"\\\"\"");
  }

  @Test
  public void testSingleQuoteInDoubleQuotes() {
    String literal = "\"\\'\"";

    for (char c : literal.toCharArray()) {
      dfa.consume(c);
    }
    dfa.consume('\n');

    Token result = dfa.getToken();
    assertEquals(result.getTokenType(), TokenType.STR_LITERAL);
    assertEquals(result.getLexeme(), "\"\\'\"");
  }

  @Test
  public void testDoubleQuoteInSingleQuotes() {
    String literal = "'\\\"'";

    for (char c : literal.toCharArray()) {
      dfa.consume(c);
    }
    dfa.consume('\n');

    Token result = dfa.getToken();
    assertEquals(result.getTokenType(), TokenType.STR_LITERAL);
    assertEquals(result.getLexeme(), "'\\\"'");
  }

  @Test
  public void testLongSingleQuoteLiteral() {
    dfa.consume('\'');
    dfa.consume('a');
    dfa.consume('b');
    assertEquals(false, dfa.consume('\''));
  }

  @Test
  public void testValidDoubleQuoteLiteral() {
    String literal = "\"abc123\\'\\\"\"";

    for (char c : literal.toCharArray()) {
      dfa.consume(c);
    }
    dfa.consume('\n');

    Token result = dfa.getToken();
    assertEquals(result.getTokenType(), TokenType.STR_LITERAL);
    assertEquals(result.getLexeme(), "\"abc123\\'\\\"\"");
  }

  @Test
  public void testEscapeCharactersInDoubleQuotes() {
    String literal = "\"\\b\\t\\n\\f\\r\\'\\\"\"";

    for (char c : literal.toCharArray()) {
      dfa.consume(c);
    }
    dfa.consume('\n');

    Token result = dfa.getToken();
    assertEquals(result.getTokenType(), TokenType.STR_LITERAL);
    assertEquals(result.getLexeme(), "\"\\b\\t\\n\\f\\r\\'\\\"\"");
  }

  @Test
  public void testEscapeOctalInDoubleQuotes() {
    String literal = "\"\\377a\"";

    for (char c : literal.toCharArray()) {
      dfa.consume(c);
    }
    dfa.consume('\n');

    Token result = dfa.getToken();
    assertEquals(result.getTokenType(), TokenType.STR_LITERAL);
    assertEquals(result.getLexeme(), "\"\\377a\"");
  }

  @Test
  public void testEscapeCharacterInSingleQuotes() {
    String literal = "'\t'";

    for (char c : literal.toCharArray()) {
      dfa.consume(c);
    }
    dfa.consume('\n');

    Token result = dfa.getToken();
    assertEquals(result.getTokenType(), TokenType.STR_LITERAL);
    assertEquals(result.getLexeme(), "'\t'");
  }

  @Test
  public void testEscapeOctalInSingleQuotes() {
    String literal = "'\\377'";

    for (char c : literal.toCharArray()) {
      dfa.consume(c);
    }
    dfa.consume('\n');

    Token result = dfa.getToken();
    assertEquals(result.getTokenType(), TokenType.STR_LITERAL);
    assertEquals(result.getLexeme(), "'\\377'");
  }

  @Test
  public void testOctalEscapeOutOfRange() {
    String literal = "'\\378'";

    dfa.consume('\'');
    dfa.consume('\\');
    dfa.consume('3');
    dfa.consume('7');
    dfa.consume('8');
    assertEquals(false, dfa.consume('\''));

    dfa.reset();
    dfa.consume('\'');
    dfa.consume('\\');
    dfa.consume('3');
    dfa.consume('8');
    dfa.consume('7');
    assertEquals(false, dfa.consume('\''));

    dfa.reset();
    dfa.consume('\'');
    dfa.consume('\\');
    dfa.consume('4');
    dfa.consume('7');
    dfa.consume('7');
    assertEquals(false, dfa.consume('\''));
  }

  @Test
  public void testEscapeMulti() {
    String literal = "\"\\\\b\"";

    for (char c : literal.toCharArray()) {
      dfa.consume(c);
    }
    dfa.consume('\n');

    Token result = dfa.getToken();
    assertEquals(result.getTokenType(), TokenType.STR_LITERAL);
    assertEquals(result.getLexeme(), "\"\\\\b\"");
  }
}