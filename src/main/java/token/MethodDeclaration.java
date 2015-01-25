package token;

import java.util.ArrayList;
import visitor.Visitee;
import visitor.Visitor;

public class MethodDeclaration extends Token implements Visitee {

  public ArrayList<Token> children;

  public MethodDeclaration(ArrayList<Token> children) {
    super("", TokenType.MethodDeclaration);
  }

  public void accept(Visitor v) {
    v.visit(this);
  }
}
