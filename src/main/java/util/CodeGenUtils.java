package util;

import token.AbstractMethodDeclaration;
import token.Declaration;
import token.FormalParameter;
import token.MethodDeclaration;

import java.io.PrintStream;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Utility class that contains common methods for code generation of x86
 */
public class CodeGenUtils {

  public static AtomicInteger ifStatementCount = new AtomicInteger(0);
  public static AtomicInteger tempCount = new AtomicInteger(0);


  /**
   * Generate a label for a given declaration using absolute path.
   */
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

  /**
   * Generate a label for a given method using it's identifier and paramater list.
   */
  public static String genMethodLabel(MethodDeclaration methodDeclaration) {
    StringBuilder sb = new StringBuilder();
    sb.append(methodDeclaration.getIdentifier());
    for (FormalParameter parameter : methodDeclaration.getParameters()) {
      sb.append('#');
      sb.append(parameter.getAbsolutePath());
    }
    if (methodDeclaration.getParameters().isEmpty()) {
      sb.append('#');
      sb.append("void");
    }
    return sb.toString();
  }
  public static String genMethodLabel(AbstractMethodDeclaration methodDeclaration) {
    StringBuilder sb = new StringBuilder();
    sb.append(methodDeclaration.getIdentifier());
    for (FormalParameter parameter : methodDeclaration.getParameters()) {
      sb.append('#');
      sb.append(parameter.getAbsolutePath());
    }
    if (methodDeclaration.getParameters().isEmpty()) {
      sb.append('#');
      sb.append("void");
    }
    return sb.toString();
  }

  /**
   * Generates a throwaway label to use for handling if-statement jmps.
   */
  public static String genNextIfStatementLabel() {
    return String.format("if#%d", ifStatementCount.getAndIncrement());
  }

  public static String genNextTempLabel() {
    return String.format("temp#%d:", tempCount.getAndIncrement());
  }

  public static void genPushRegisters(PrintStream output) {
    output.println("push eax");
    output.println("push ebx");
    output.println("push ecx");
    output.println("push edx");
  }

  public static void genPopRegisters(PrintStream output) {
    output.println("pop eax");
    output.println("pop ebx");
    output.println("pop ecx");
    output.println("pop edx");
  }

}
