package exception;

import token.Token;

/**
 * Represents a failure during static evaluation
 */
public class StaticEvaluationVisitorException extends VisitorException {
    public StaticEvaluationVisitorException(String message, Token token) {
      super(message, token);
    }
}
