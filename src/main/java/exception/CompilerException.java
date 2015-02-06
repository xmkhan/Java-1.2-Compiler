package exception;

import token.Token;

/**
 * Base compiler exception
 */
public class CompilerException extends Exception {
  public CompilerException(String msg) {
    super(msg);
  }

  public CompilerException(String msg, Token token) {
    super(msg + (token != null ? token.getErrMsgLocation() : ""));
  }
}
