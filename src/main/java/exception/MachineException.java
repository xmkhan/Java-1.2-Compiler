package exception;

import token.Token;

/**
 * Created by ali on 06/02/15.
 */
public class MachineException extends CompilerException {
  public MachineException(String message, Token token) {
    super(message, token);
  }
}