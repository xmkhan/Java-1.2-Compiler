package token;

import java.util.ArrayList;
import visitor.Visitee;
import visitor.Visitor;

public class ConstructorBody extends Token implements Visitee {

  public ArrayList<Token> children;

  public ConstructorBody(ArrayList<Token> children) {
    super("", TokenType.ConstructorBody);
  }

  public void accept(Visitor v) {
    v.visit(this);
  }
}
