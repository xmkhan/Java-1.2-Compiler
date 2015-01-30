package dfa;

import org.junit.Before;
import org.junit.Test;
import token.Token;
import token.TokenType;

import static org.junit.Assert.assertEquals;

/**
 * Basic tests to test the CommentDFA
 */
public class CommentDFATest {
  private CommentDFA dfa;
  private char[] delimiters = {'\n', '\r'};

  @Before
  public void setUp() {
    dfa = new CommentDFA();
  }

  @Test
  public void testDoubleSlashComment() {
    String comment = "// this is a comment.\n";
    consumeString(comment);
    assertEqualsToken(dfa.getToken(), TokenType.COMMENT_SLASH, comment);
  }

  @Test
  public void testNestedDoubleSlashComment() {
    String comment = "// this is a/* */nested // comment public static class final //\n";
    consumeString(comment);
    assertEqualsToken(dfa.getToken(), TokenType.COMMENT_SLASH, comment);
  }

  @Test
  public void testNewLineDoubleSlashComment() {
    String[] comments = new String[] {
        "// this is a comment1\r", "// this is a comment2\n", "// this is a comment3\r\n",
    };

    for (String comment : comments) {
      dfa.reset();
      consumeString(comment);
      assertEqualsToken(dfa.getToken(), TokenType.COMMENT_SLASH, comment);
    }
  }

  @Test
  public void testSlashStarComment() {
    String comment = "/* this is a comment */";
    consumeString(comment);
    assertEqualsToken(dfa.getToken(), TokenType.COMMENT_STAR, comment);
  }

  @Test
  public void testSlashStarMultilineComment() {
    String comment = "/* this is a comment \n\n\n\r\n and its still going */";
    consumeString(comment);
    assertEqualsToken(dfa.getToken(),TokenType.COMMENT_STAR, comment);
  }

  @Test
  public void testNestedSlashStarComment() {
    String comment = "/** this is a comment public void star // /* /* nested /***/";
    consumeString(comment);
    assertEqualsToken(dfa.getToken(),TokenType.COMMENT_STAR, comment);
  }

  @Test
  public void testIncompleteSlashStarComment() {
    String comment = "/* this is a comment */ */";
    consumeString(comment);
    assertEqualsToken(dfa.getToken(), TokenType.COMMENT_STAR, "/* this is a comment */");
  }

  private void consumeString(String comment) {
    for (char c : comment.toCharArray()) {
      dfa.consume(c);
    }
    dfa.consume(delimiters[(int)(Math.random() * delimiters.length)]);
  }

  private void assertEqualsToken(Token result, TokenType type, String comment) {
    assertEquals(type, result.getTokenType());
    assertEquals(comment, result.getLexeme());
  }
}
