package util;

import token.Declaration;
import token.FormalParameter;
import token.MethodDeclaration;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Utility class that contains common methods for code generation of x86
 */
public class CodeGenUtils {

  public static AtomicInteger ifStatementCount = new AtomicInteger(0);

  public static String genLabel(Declaration declaration) {
    StringBuilder sb = new StringBuilder();
    sb.append(declaration.getAbsolutePath());
    if (declaration instanceof MethodDeclaration) {
      MethodDeclaration methodDeclaration = (MethodDeclaration) declaration;
      List<FormalParameter> parameters = methodDeclaration.getParameters();
      if (parameters.isEmpty()) {
        sb.append('#');
        sb.append("void");
      } else {
        for (FormalParameter param : parameters) {
          sb.append('#');
          sb.append(param.getAbsolutePath());
        }
      }
      sb.append(":");
    }
    return sb.toString();
  }

}
