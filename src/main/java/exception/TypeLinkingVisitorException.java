package exception;

/**
 * An exception representing a failure during Symbol table construction.
 */
public class TypeLinkingVisitorException extends CompilerException {
  public TypeLinkingVisitorException(String msg) {
    super(msg);
  }
}
