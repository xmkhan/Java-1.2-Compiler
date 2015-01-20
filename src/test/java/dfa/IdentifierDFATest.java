package dfa;

import dfa.IdentifierDFA;
import org.junit.Test;
import token.Token;
import token.TokenType;

import static org.junit.Assert.assertTrue;

/**
 * Basic tests to test the Identifier DFA.
 */
public class IdentifierDFATest {

  @Test
  public void testOneChar()
  {
    IdentifierDFA dfa = new IdentifierDFA();

    dfa.consume('d');
    dfa.consume(' ');

    Token result = dfa.getToken();
    assertTrue(result.getTokenType() == TokenType.IDENTIFIER);
    assertTrue(result.getLexeme().equals("d"));
  }

  @Test
  public void testOneCharSpecial()
  {
    IdentifierDFA dfa = new IdentifierDFA();

    dfa.consume('$');
    dfa.consume(' ');

    Token result = dfa.getToken();
    assertTrue(result.getTokenType() == TokenType.IDENTIFIER);
    assertTrue(result.getLexeme().equals("$"));
  }

  @Test
  public void testJavaString()
  {
    IdentifierDFA dfa = new IdentifierDFA();
    String javaString = "abcdefghijklmnoqprstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ_$";

    for(char c: javaString.toCharArray())
    {
      dfa.consume(c);
    }
    dfa.consume('\r');

    Token result = dfa.getToken();
    assertTrue(result.getTokenType() == TokenType.IDENTIFIER);
    assertTrue(result.getLexeme().equals(javaString));
  }

  @Test
  public void testJavaDigitsAndLetters()
  {
    IdentifierDFA dfa = new IdentifierDFA();
    String javaString = "test0123456789";

    for(char c: javaString.toCharArray())
    {
      dfa.consume(c);
    }
    dfa.consume('\n');

    Token result = dfa.getToken();
    assertTrue(result.getTokenType() == TokenType.IDENTIFIER);
    assertTrue(result.getLexeme().equals(javaString));
  }

  @Test
  public void testJavaDigitsAndLettersProper()
  {
    IdentifierDFA dfa = new IdentifierDFA();
    String javaString = "_test43 = 52;\n";

    for(char c: javaString.toCharArray())
    {
      dfa.consume(c);
    }

    Token result = dfa.getToken();
    assertTrue(result.getTokenType() == TokenType.IDENTIFIER);
    assertTrue(result.getLexeme().equals("_test43"));
  }

  @Test
  public void testJavaDigits()
  {
    IdentifierDFA dfa = new IdentifierDFA();
    String javaString = "01234test";

    for(char c: javaString.toCharArray())
    {
      dfa.consume(c);
    }
    dfa.consume(' ');

    assertTrue(dfa.getToken() == null);
  }

  @Test
  public void testOneDigit()
  {
    IdentifierDFA dfa = new IdentifierDFA();

    dfa.consume('4');
    dfa.consume(' ');

    assertTrue(dfa.getToken() == null);
  }

  @Test
  public void testEmpty()
  {
    IdentifierDFA dfa = new IdentifierDFA();
    assertTrue(dfa.getToken() == null);
  }

  @Test
  public void testInvalid()
  {
    IdentifierDFA dfa = new IdentifierDFA();
    String javaString = "ab3243^&abcd";

    for(char c: javaString.toCharArray())
    {
      dfa.consume(c);
    }
    dfa.consume(' ');

    Token result = dfa.getToken();
    assertTrue(result.getTokenType() == TokenType.IDENTIFIER);
    assertTrue(result.getLexeme().equals("ab3243"));
  }

  @Test
  public void testInvalidEscapes()
  {
    IdentifierDFA dfa = new IdentifierDFA();
    String javaString = "\"abcdef33\'";

    for(char c: javaString.toCharArray())
    {
      dfa.consume(c);
    }
    dfa.consume(' ');

    assertTrue(dfa.getToken() == null);
  }
}