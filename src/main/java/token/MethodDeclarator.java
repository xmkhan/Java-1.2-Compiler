package token;

import java.util.ArrayList;
import visitor.Visitee;
import visitor.Visitor;

public class MethodDeclarator extends Token implements Visitee {

  public ArrayList<Token> children;

  public MethodDeclarator(ArrayList<Token> children) {
    super("", TokenType.MethodDeclarator);
  }

  public void accept(Visitor v) {
    v.visit(this);
  }
}
