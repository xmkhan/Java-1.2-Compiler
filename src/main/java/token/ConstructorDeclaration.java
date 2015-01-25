package token;

import java.util.ArrayList;
import visitor.Visitee;
import visitor.Visitor;

public class ConstructorDeclaration extends Token implements Visitee {

  public ArrayList<Token> children;

  public ConstructorDeclaration(ArrayList<Token> children) {
    super("", TokenType.ConstructorDeclaration);
  }

  public void accept(Visitor v) {
    v.visit(this);
  }
}
