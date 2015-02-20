package exception;

/**
 * An exception representing a failure during Symbol table construction.
 */
public class TypeLinkingException extends CompilerException {
  public TypeLinkingException(String msg) {
    super(msg);
  }
}
