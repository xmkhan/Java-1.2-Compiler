package token;

import java.util.ArrayList;
import visitor.Visitee;
import visitor.Visitor;

public class VariableDeclarator extends Token implements Visitee {

  public ArrayList<Token> children;

  public VariableDeclarator(ArrayList<Token> children) {
    super("", TokenType.VariableDeclarator);
  }

  public void accept(Visitor v) {
    v.visit(this);
  }
}
