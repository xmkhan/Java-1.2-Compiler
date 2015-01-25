package token;

import java.util.ArrayList;
import visitor.Visitee;
import visitor.Visitor;

public class CompilationUnit extends Token implements Visitee {

  public ArrayList<Token> children;

  public CompilationUnit(ArrayList<Token> children) {
    super("", TokenType.CompilationUnit);
  }

  public void accept(Visitor v) {
    v.visit(this);
  }
}
