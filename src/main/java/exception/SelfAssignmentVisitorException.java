package exception;

import token.Token;

/**
 * Represents a failure for resolving self assignment of undeclared variables.
 */
public class SelfAssignmentVisitorException extends VisitorException {
  public SelfAssignmentVisitorException(String message, Token token) {
    super(message, token);
  }
}
