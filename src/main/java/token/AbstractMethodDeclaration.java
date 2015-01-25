package token;

import java.util.ArrayList;
import visitor.Visitee;
import visitor.Visitor;

public class AbstractMethodDeclaration extends Token implements Visitee {

  public ArrayList<Token> children;

  public AbstractMethodDeclaration(ArrayList<Token> children) {
    super("", TokenType.AbstractMethodDeclaration);
  }

  public void accept(Visitor v) {
    v.visit(this);
  }
}
