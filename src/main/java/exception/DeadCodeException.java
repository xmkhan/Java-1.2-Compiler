package exception;

/**
 * At the later stages of compiler construction some checks should not fail.
 * If they do, however, use this exception.
 * For example during HierarchyChecking a Token is supposed to be of type
 * ClassDeclaration, but it is not.  Instead of simply ignoring it, throw
 * this exception. 
 */
public class DeadCodeException extends CompilerException {
  public DeadCodeException(String msg) {
    super(msg);
  }
}
