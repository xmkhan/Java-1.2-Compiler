import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class TokenFileGenerator {

  public static void writeTokenFile(String directoryPath, String tokenName) throws IOException
  {
    PrintWriter writer = new PrintWriter(new FileWriter(new File(directoryPath + "/token/" + tokenName + ".java")));

    writer.println("package token;");
    writer.println();
    writer.println("import java.util.ArrayList;");
    writer.println("import visitor.Visitor;");
    writer.println();
    writer.println("public class " + tokenName + " extends Token {");
    writer.println();
    writer.println("  public ArrayList<Token> children;");
    writer.println();
    writer.println("  public " + tokenName + "(ArrayList<Token> children) {");
    writer.println("    super(\"\", TokenType." + tokenName + ");");
    writer.println("    this.children = children;");
    writer.println("  }");
    writer.println();
    writer.println("  public void accept(Visitor v) {");
    writer.println("    for (Token token : children) {");
    writer.println("      token.accept(v);");
    writer.println("    }");
    writer.println("    v.visit(this);");
    writer.println("  }");
    writer.println("}");

    writer.close();
  }

  public static void writeVisitor(String directoryPath, Set<String> tokens, String prefix) throws IOException {
    PrintWriter writer = new PrintWriter(new FileWriter(new File(directoryPath + "/visitor/" + prefix + "Visitor.java")));

    writer.println("package visitor;");

    writer.println();
    writer.println("import token.*;");
    writer.println();

    if(prefix.equals("")) {
      writer.println("public interface Visitor {");
    } else {
      writer.println("public class BaseVisitor implements Visitor {");
    }
    writer.println();

    for(String token : tokens) {
      if(prefix.equals("")) {
        writer.println("  public void visit(" + token + " token);");
      } else {
        writer.println("  public void visit(" + token + " token) {");
        writer.println("  }");
      }
      writer.println();
    }

    // Add a visitor for the token base class.
    if (prefix.equals("")) {
      writer.println("  public void visit(Token token);");
    } else {
      writer.println("  public void visit(Token token) {");
      writer.println("  }");
    }
    writer.println();

    writer.println("}");
    writer.close();
  }

  public static void writeEnum(String directoryPath, Set<String> nonTerminals) throws IOException {
    PrintWriter writer = new PrintWriter(new FileWriter(new File(directoryPath + "/Enum/enum.temp")));

    for (String nonTerminal : nonTerminals) {
      writer.println("  " + nonTerminal + "(\"" + nonTerminal + "\"),");
    }

    writer.close();
  }

  public static void main(String[] args) throws IOException {
    BufferedReader reader = new BufferedReader(new FileReader(new File(args[0])));
    Set<String> terminals = new HashSet<String>(), nonTerminals = new HashSet<String>();

    int numTerminals = Integer.parseInt(reader.readLine());
    for(int a = 0; a < numTerminals; a++) {
      terminals.add(reader.readLine());
    }

    int numNonTerminals = Integer.parseInt(reader.readLine());
    for(int a = 0; a < numNonTerminals; a++) {
      nonTerminals.add(reader.readLine());
    }

    reader.close();

    String directoryPath = "./"; //new File(args[0]).getParent();

    HashSet<String> toGenerate = new HashSet<String>();
    toGenerate.addAll(nonTerminals);
    for(String terminal : terminals) {
      if(terminal.endsWith("Literal")) {
        toGenerate.add(terminal);
      }
    }

    for(String token : toGenerate) {
      writeTokenFile(directoryPath, token);
    }
    writeVisitor(directoryPath, toGenerate, "");
    writeVisitor(directoryPath, toGenerate, "Base");
    writeEnum(directoryPath, nonTerminals);
  }
}
