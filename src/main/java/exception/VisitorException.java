package exception;

import token.Token;

public class VisitorException extends CompilerException {

  public VisitorException(String message, Token token) {
    super(message, token);
  }

}
