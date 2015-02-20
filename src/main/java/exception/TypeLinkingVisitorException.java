package exception;

import token.Token;

/**
 * An exception representing a failure during Symbol table construction.
 */
public class TypeLinkingVisitorException extends VisitorException {
  public TypeLinkingVisitorException(String message, Token token) {
    super(message, token);
  }
}
