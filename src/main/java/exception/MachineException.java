package exception;

import token.Token;

/**
 * An exception representing failures thrown by the LR(1) Machine
 */
public class MachineException extends CompilerException {
  public MachineException(String message, Token token) {
    super(message, token);
  }
}