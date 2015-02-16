package exception;

/**
 * An exception representing a failure during Symbol table construction.
 */
public class SymbolTableException extends CompilerException {
  public SymbolTableException(String msg) {
    super(msg);
  }
}
