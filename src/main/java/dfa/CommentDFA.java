package dfa;

import token.Token;
import token.TokenType;

/**
 * Handles parsing of comments.
 * <p/>
 * Requirements:
 * Supports comments "//...\n", /*, /**
 */
public class CommentDFA implements DFA {
  private StringBuilder builder;
  private Token token;

  private enum states {ERROR, START, SINGLE_SLASH, DOUBLE_SLASH, SLASH_STAR, STAR_SLASH, ACCEPT}

  private states state;

  private TokenType comment_type;
  private boolean isCRLF;

  public CommentDFA() {
    reset();
  }

  @Override
  public void reset() {
    state = states.START;
    builder = new StringBuilder();
    token = null;
    comment_type = TokenType.NOT_USED;
    isCRLF = false;
  }

  @Override
  public boolean consume(char c) {
    switch (state) {
      case ERROR:
        break;
      case START:
        if (c == '/') {
          state = states.SINGLE_SLASH;
          builder.append(c);
        } else state = states.ERROR;
        break;
      case SINGLE_SLASH:
        if (c == '/') {
          state = states.DOUBLE_SLASH;
          builder.append(c);
        } else if (c == '*') {
          state = states.SLASH_STAR;
          builder.append(c);
        } else state = states.ERROR;
        break;
      case DOUBLE_SLASH:
        if (c == '\n' || c == '\r') {
          comment_type = TokenType.COMMENT_SLASH;
          state = states.ACCEPT;
          if (c == '\r') isCRLF = true;
        }
        builder.append(c);
        break;
      case SLASH_STAR:
        if (c == '*') {
          state = state.STAR_SLASH;
        }
        builder.append(c);
        break;
      case STAR_SLASH:
        if (c == '/') {
          comment_type = TokenType.COMMENT_STAR;
          state = state.ACCEPT;
        } else if (c != '*') {
          state = state.SLASH_STAR;
        }
        builder.append(c);
        break;
      case ACCEPT:
        if (c == '\n' && isCRLF) {
          isCRLF = false;
          builder.append(c);
        } else {
          token = new Token(builder.toString(), comment_type);
          state = states.ERROR;
        }
        break;
    }
    return state != states.ERROR;
  }

  @Override
  public Token getToken() {
    return token;
  }
}
