package exception;

import token.Token;

/**
 * An exception representing failures for variable name resolution by VariableNameResolutionAlgorithm
 */
public class VariableNameResolutionException extends CompilerException {
  public VariableNameResolutionException(String msg, Token token) {
    super(msg, token);
  }

  public VariableNameResolutionException(String msg) {
    super(msg);
  }
}
