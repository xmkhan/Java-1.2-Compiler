package dfa;

import org.junit.Before;
import org.junit.Test;
import token.Token;
import token.TokenType;

import static org.junit.Assert.assertEquals;

/**
 * Basic tests to test the ReservedDFA
 */
public class ReservedDFATest {
  private ReservedDFA dfa;
  private char[] delimiters = {'\n', '\r', '\t', ' '};

  @Before
  public void setUp() {
    dfa = new ReservedDFA();
  }

  @Test
  public void testSingleReservedWords() {
    for (int i = 0; i < TokenType.RESERVED_LENGTH.ordinal(); ++i) {
      dfa.reset();
      String word = TokenType.values()[i].toString();
      consumeString(word);
      assertEqualsTokenType(TokenType.values()[i], dfa.getToken());
    }
  }

  @Test
  public void invalidReservedWords() {
    String[] invalidWords = new String[]{
        "publix", "suepr", "floaat", "du\tble", "clazsic", "pu blic", "in\nt"
    };
    for (String word : invalidWords) {
      dfa.reset();
      consumeString(word);
      assertEquals(dfa.getToken(), null);
    }
  }

  @Test
  public void testNotSeparatedWords() {
    String[] notSeparatedWords = new String[]{
        "publicstatic", "finalint", "publicpublic"
    };
    String[] expectedNotSeparatedWords = new String[]{
        "public", "final", "public"
    };
    for (int i = 0; i < notSeparatedWords.length && i < expectedNotSeparatedWords.length; ++i) {
      dfa.reset();
      String word = notSeparatedWords[i];
      consumeString(word);
      String expectedWord = expectedNotSeparatedWords[i];
      assertEquals(dfa.getToken().getLexeme(), expectedWord);
    }
  }


  private void consumeString(String word) {
    for (char c : word.toCharArray()) {
      dfa.consume(c);
    }
    dfa.consume(delimiters[(int) (Math.random() * delimiters.length)]);
  }

  private void assertEqualsTokenType(TokenType type, Token token) {
    assertEquals(type, token.getTokenType());
  }
}
