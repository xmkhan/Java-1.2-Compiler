package token;

import java.util.ArrayList;
import visitor.Visitee;
import visitor.Visitor;

public class ConstructorDeclarator extends Token implements Visitee {

  public ArrayList<Token> children;

  public ConstructorDeclarator(ArrayList<Token> children) {
    super("", TokenType.ConstructorDeclarator);
  }

  public void accept(Visitor v) {
    v.visit(this);
  }
}
