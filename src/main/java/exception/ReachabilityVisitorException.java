package exception;

import token.Token;

/**
 * Represents a failure during reachability.
 */
public class ReachabilityVisitorException extends VisitorException {
  public ReachabilityVisitorException(String message, Token token) {
    super(message, token);
  }
}
