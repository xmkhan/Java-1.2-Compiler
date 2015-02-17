package exception;

/**
 * Exception for any failures encountered during the creation of an environment.
 */
public class EnvironmentBuildingException extends CompilerException {
  public EnvironmentBuildingException(String msg) {
    super(msg);
  }
}
