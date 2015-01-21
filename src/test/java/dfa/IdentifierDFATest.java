package dfa;

import org.junit.Before;
import org.junit.Test;
import token.Token;
import token.TokenType;

import static org.junit.Assert.assertEquals;

/**
 * Basic tests to test the Identifier DFA.
 */
public class IdentifierDFATest {

  private IdentifierDFA dfa;

  @Before
  public void setUp() {
    dfa = new IdentifierDFA();
  }

  @Test
  public void testOneChar() {
    dfa.consume('d');
    dfa.consume(' ');

    Token result = dfa.getToken();
    assertEquals(TokenType.IDENTIFIER, result.getTokenType());
    assertEquals("d", result.getLexeme());
  }

  @Test
  public void testOneCharSpecial() {
    dfa.consume('$');
    dfa.consume(' ');

    Token result = dfa.getToken();
    assertEquals(TokenType.IDENTIFIER, result.getTokenType());
    assertEquals("$", result.getLexeme());
  }

  @Test
  public void testJavaString() {
    String javaString = "abcdefghijklmnoqprstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ_$";

    for(char c: javaString.toCharArray())
    {
      dfa.consume(c);
    }
    dfa.consume('\r');

    Token result = dfa.getToken();
    assertEquals(TokenType.IDENTIFIER, result.getTokenType());
    assertEquals(javaString, result.getLexeme());
  }

  @Test
  public void testJavaDigitsAndLetters() {
    String javaString = "test0123456789";

    for(char c: javaString.toCharArray())
    {
      dfa.consume(c);
    }
    dfa.consume('\n');

    Token result = dfa.getToken();
    assertEquals(TokenType.IDENTIFIER, result.getTokenType());
    assertEquals(javaString, result.getLexeme());
  }

  @Test
  public void testJavaDigitsAndLettersProper() {
    String javaString = "_test43 = 52;\n";

    for(char c: javaString.toCharArray()) {
      dfa.consume(c);
    }

    Token result = dfa.getToken();
    assertEquals(TokenType.IDENTIFIER, result.getTokenType());
    assertEquals("_test43", result.getLexeme());
  }

  @Test
  public void testJavaDigits() {
    String javaString = "01234test";

    for(char c: javaString.toCharArray()) {
      dfa.consume(c);
    }
    dfa.consume(' ');

    assertEquals(null, dfa.getToken());
  }

  @Test
  public void testOneDigit() {
    dfa.consume('4');
    dfa.consume(' ');

    assertEquals(null, dfa.getToken());
  }

  @Test
  public void testEmpty() {
    assertEquals(null, dfa.getToken());
  }

  @Test
  public void testInvalid() {
    String javaString = "ab3243^&abcd";

    for(char c: javaString.toCharArray()) {
      dfa.consume(c);
    }
    dfa.consume(' ');

    Token result = dfa.getToken();
    assertEquals(TokenType.IDENTIFIER, result.getTokenType());
    assertEquals("ab3243", result.getLexeme());
  }

  @Test
  public void testInvalidEscapes() {
    String javaString = "\"abcdef33\'";

    for(char c: javaString.toCharArray()) {
      dfa.consume(c);
    }
    dfa.consume(' ');

    assertEquals(null, dfa.getToken());
  }
}