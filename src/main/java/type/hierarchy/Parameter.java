package type.hierarchy;

/**
 * Represents a method parameter
 */
public class Parameter {
  public String type;
  public boolean isArray;

  public Parameter(String type, boolean isArray) {
    this.type = type;
    this.isArray = isArray;
  }
}
