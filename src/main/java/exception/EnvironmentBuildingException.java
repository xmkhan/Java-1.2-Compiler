package exception;

import token.Token;

/**
 * Exception for any failures encountered during the creation of an environment.
 */
public class EnvironmentBuildingException extends VisitorException {
  public EnvironmentBuildingException(String message, Token token) {
    super(message, token);
  }
}
