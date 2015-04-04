package util;

import token.AbstractMethodDeclaration;
import token.ConstructorDeclaration;
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
  public static AtomicInteger elseStatementCount = new AtomicInteger(0);
  public static AtomicInteger forStatementCount = new AtomicInteger(0);
  public static AtomicInteger whileStmtCount = new AtomicInteger(0);

  public static AtomicInteger labelCounter = new AtomicInteger(0);

  /**
   * Generate a label for a given declaration using absolute path.
   */
  public static String genLabel(Declaration declaration) {
    StringBuilder sb = new StringBuilder();
    sb.append(declaration.getAbsolutePath());
    if (declaration instanceof MethodDeclaration || declaration instanceof ConstructorDeclaration) {
      List<FormalParameter> parameters;
      if (declaration instanceof MethodDeclaration) {
        parameters = ((MethodDeclaration) declaration).getParameters();
      } else {
        parameters = ((ConstructorDeclaration) declaration).getParameters();
      }
      if (parameters.isEmpty()) {
        sb.append('#');
        sb.append("void");
      } else {
        for (FormalParameter param : parameters) {
          sb.append('#');
          sb.append(param.getTypeString());
        }
      }
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
      sb.append(parameter.getTypeString());
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
      sb.append(parameter.getTypeString());
    }
    if (methodDeclaration.getParameters().isEmpty()) {
      sb.append('#');
      sb.append("void");
    }
    return sb.toString();
  }

  /**
   * Generates a throwaway label to use for handling if-elseStatement jmps.
   */
  public static String genNextIfStatementLabel() {
    return String.format("if#%d", ifStatementCount.getAndIncrement());
  }
  public static String genUniqueLabel() {
    return String.format("label%d", labelCounter.getAndIncrement());
  }

  /**
   * used for jumping to the end of if-else-then statements
   */
  public static String getCurrentElseStmtLabel() {
    return String.format("else#%d", elseStatementCount.get());
  }

  /**
   * Use for creating the final else label for exiting
   * if-else-then statements
   */
  public static String genNextElseStmtLabel() {
    return String.format("else#%d", elseStatementCount.getAndIncrement());
  }

  /**
   * Generates a throwaway label to use for handling for-loop jmps.
   */
  public static String genNextForStatementLabel() {
    return String.format("for#%d", forStatementCount.getAndIncrement());
  }

  /*
   * Generates a throwaway label to use for handling if-statement jmps.
   */
  public static String genNextWhileStmtLabel() {
    return String.format("while#%d", whileStmtCount.getAndIncrement());
  }

  public static void genPushRegisters(PrintStream output) {
    output.println("push eax");
    output.println("push ebx");
    output.println("push ecx");
    output.println("push edx");
  }

  public static void genPopRegisters(PrintStream output) {
    output.println("pop edx");
    output.println("pop ecx");
    output.println("pop ebx");
    output.println("pop eax");
  }

  /**
   * Currently, we assume that the size of all data types is 4 bytes. However, this method is invoked so that in the
   * feature, we may support data types of different sizes: 1, 2, or 4 bytes with the appropriate mov/registers.
   */
  public static int getSize(String type) {
    /**
    if (type.equals("boolean")) return 1;
    else if (type.equals("int")) return 4;
    else if (type.equals("char")) return 1;
    else if (type.equals("byte")) return 1;
    else if (type.equals("short")) return 2;
    */
    return 4;
  }

  public static String getReserveSize(int size) {
    switch (size) {
      case 1:
        return "db";
      case 2:
        return "dw";
      case 4:
      default:
        return "dd";
    }
  }

}
