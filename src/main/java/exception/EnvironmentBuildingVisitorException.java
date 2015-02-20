package exception;

import token.Token;

/**
 * Exception for any failures encountered during the creation of an environment.
 */
public class EnvironmentBuildingVisitorException extends VisitorException {
  public EnvironmentBuildingVisitorException(String message, Token token) {
    super(message, token);
  }
}
