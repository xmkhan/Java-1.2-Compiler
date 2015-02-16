package exception;

/**
 * Exception for any type hierarchy failures.
 */
public class TypeHierarchyException extends CompilerException {
  public TypeHierarchyException(String msg) {
    super(msg);
  }
}
