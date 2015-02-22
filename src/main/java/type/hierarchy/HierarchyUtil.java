package type.hierarchy;

import token.ImportDeclaration;

import java.util.List;

/**
 * Includes utility functions used for hierarchy checking
 */
public class HierarchyUtil {
  public static boolean checkWithImports(List<ImportDeclaration> imports, String a, String b) {
    for (ImportDeclaration imported : imports) {
      String importPrefix = imported.getLexeme() + (imported.onDemand ? "." + a : "");
      if (importPrefix.equals(b)) {
        return true;
      }
    }
    return false;
  }
}
