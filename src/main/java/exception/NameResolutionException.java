package exception;

/**
 * An exception representing failures for name resolution by NameResolutionAlgorithm.
 */
public class NameResolutionException extends CompilerException {
  public NameResolutionException(String msg) {
    super(msg);
  }
}
