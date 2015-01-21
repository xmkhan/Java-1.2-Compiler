package dfa;

import org.junit.Test;
import token.Token;
import token.TokenType;

import static org.junit.Assert.assertTrue;

/**
 * Basic tests to test the Numeric DFA.
 */
public class NumericDFATest {

  @Test
  public void testOneDigit() {
    NumericDFA dfa = new NumericDFA();

    dfa.consume('4');
    dfa.consume(' ');

    Token result = dfa.getToken();
    assertTrue(result.getTokenType() == TokenType.INT_LITERAL);
    assertTrue(result.getLexeme().equals("4"));
  }

  @Test
  public void testJavaNumber() {
    NumericDFA dfa = new NumericDFA();
    String javaString = "1234567890";

    for(char c: javaString.toCharArray()) {
      dfa.consume(c);
    }
    dfa.consume('\r');

    Token result = dfa.getToken();
    assertTrue(result.getTokenType() == TokenType.INT_LITERAL);
    assertTrue(result.getLexeme().equals(javaString));
  }

  @Test
  public void testJavaDigitsAndLetters() {
    NumericDFA dfa = new NumericDFA();
    String javaString = "123t9";

    for(char c: javaString.toCharArray()) {
      dfa.consume(c);
    }
    dfa.consume('\n');

    Token result = dfa.getToken();
    assertTrue(result.getTokenType() == TokenType.INT_LITERAL);
    assertTrue(result.getLexeme().equals("123"));
  }

  @Test
  public void testOneCharSpecial() {
    NumericDFA dfa = new NumericDFA();

    dfa.consume('$');
    dfa.consume(' ');

    assertTrue(dfa.getToken() == null);
  }

  @Test
  public void testOneChar() {
    NumericDFA dfa = new NumericDFA();

    dfa.consume('c');
    dfa.consume(' ');

    assertTrue(dfa.getToken() == null);
  }

  @Test
  public void testInvalidJavaDigitsAndLetters() {
    NumericDFA dfa = new NumericDFA();
    String javaString = "test343";

    for(char c: javaString.toCharArray()) {
      dfa.consume(c);
    }
    dfa.consume('\n');

    assertTrue(dfa.getToken() == null);
  }

  @Test
  public void testInvalidJavaDigits() {
    NumericDFA dfa = new NumericDFA();
    String javaString = "01234";

    for(char c: javaString.toCharArray()) {
      dfa.consume(c);
    }

    Token result = dfa.getToken();
    assertTrue(result.getTokenType() == TokenType.INT_LITERAL);
    assertTrue(result.getLexeme().equals("0"));

    dfa.reset();
    javaString = "12340";

    for(char c: javaString.toCharArray()) {
      dfa.consume(c);
    }
    dfa.consume(' ');

    result = dfa.getToken();
    assertTrue(result.getTokenType() == TokenType.INT_LITERAL);
    assertTrue(result.getLexeme().equals("12340"));
  }

  @Test
  public void testEmpty() {
    NumericDFA dfa = new NumericDFA();
    assertTrue(dfa.getToken() == null);
  }

  @Test
  public void testInvalid() {
    NumericDFA dfa = new NumericDFA();
    String javaString = "1234^&abcd";

    for(char c: javaString.toCharArray()) {
      dfa.consume(c);
    }
    dfa.consume(' ');

    Token result = dfa.getToken();
    assertTrue(result.getTokenType() == TokenType.INT_LITERAL);
    assertTrue(result.getLexeme().equals("1234"));
  }
}