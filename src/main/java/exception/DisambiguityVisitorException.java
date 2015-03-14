package exception;

import token.Token;

/**
 * Represents an error in resolving disambiguity of a Name.
 */
public class DisambiguityVisitorException extends VisitorException {
  public DisambiguityVisitorException(String message, Token token) {
    super(message, token);
  }
}
