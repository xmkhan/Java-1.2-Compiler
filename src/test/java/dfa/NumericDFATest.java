package dfa;

import org.junit.Before;
import org.junit.Test;
import token.Token;
import token.TokenType;

import static org.junit.Assert.assertEquals;

/**
 * Basic tests to test the Numeric DFA.
 */
public class NumericDFATest {
  private NumericDFA dfa;

  @Before
  public void setUp() {
    dfa = new NumericDFA();
  }

  @Test
  public void testOneDigit() {
    dfa.consume('4');
    dfa.consume(' ');

    Token result = dfa.getToken();
    assertEquals(TokenType.INT_LITERAL, result.getTokenType());
    assertEquals("4", result.getLexeme());
  }

  @Test
  public void testJavaNumber() {
    String javaString = "1234567890";

    for (char c : javaString.toCharArray()) {
      dfa.consume(c);
    }
    dfa.consume('\r');

    Token result = dfa.getToken();
    assertEquals(TokenType.INT_LITERAL, result.getTokenType());
    assertEquals(javaString, result.getLexeme());
  }

  @Test
  public void testJavaDigitsAndLetters() {
    String javaString = "123t9";

    for (char c : javaString.toCharArray()) {
      dfa.consume(c);
    }
    dfa.consume('\n');

    Token result = dfa.getToken();
    assertEquals(TokenType.INT_LITERAL, result.getTokenType());
    assertEquals("123", result.getLexeme());
  }

  @Test
  public void testOneCharSpecial() {
    dfa.consume('$');
    dfa.consume(' ');

    assertEquals(null, dfa.getToken());
  }

  @Test
  public void testOneChar() {
    dfa.consume('c');
    dfa.consume(' ');

    assertEquals(null, dfa.getToken());
  }

  @Test
  public void testInvalidJavaDigitsAndLetters() {
    String javaString = "test343";

    for (char c : javaString.toCharArray()) {
      dfa.consume(c);
    }
    dfa.consume('\n');

    assertEquals(null, dfa.getToken());
  }

  @Test
  public void testInvalidJavaDigits() {
    String javaString = "01234";

    for (char c : javaString.toCharArray()) {
      dfa.consume(c);
    }

    Token result = dfa.getToken();
    assertEquals(TokenType.INT_LITERAL, result.getTokenType());
    assertEquals("0", result.getLexeme());

    dfa.reset();
    javaString = "12340";

    for (char c : javaString.toCharArray()) {
      dfa.consume(c);
    }
    dfa.consume(' ');

    result = dfa.getToken();
    assertEquals(TokenType.INT_LITERAL, result.getTokenType());
    assertEquals("12340", result.getLexeme());
  }

  @Test
  public void testEmpty() {
    assertEquals(null, dfa.getToken());
  }

  @Test
  public void testInvalid() {
    String javaString = "1234^&abcd";

    for (char c : javaString.toCharArray()) {
      dfa.consume(c);
    }
    dfa.consume(' ');

    Token result = dfa.getToken();
    assertEquals(TokenType.INT_LITERAL, result.getTokenType());
    assertEquals("1234", result.getLexeme());
  }
}