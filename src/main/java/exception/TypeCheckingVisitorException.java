package exception;

import token.Token;

/**
 * An exception representing a failure during type checking
 */
public class TypeCheckingVisitorException extends VisitorException {
  public TypeCheckingVisitorException(String message, Token token) {
    super(message, token);
  }
}
