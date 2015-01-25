package token;

import java.util.ArrayList;
import visitor.Visitee;
import visitor.Visitor;

public class MethodBody extends Token implements Visitee {

  public ArrayList<Token> children;

  public MethodBody(ArrayList<Token> children) {
    super("", TokenType.MethodBody);
  }

  public void accept(Visitor v) {
    v.visit(this);
  }
}
