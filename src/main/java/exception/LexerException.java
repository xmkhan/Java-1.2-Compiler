package exception;

/**
 * An exception thrown for failures during lexing.
 */
public class LexerException extends CompilerException {

  public LexerException(String message) {
    super(message);
  }
}
