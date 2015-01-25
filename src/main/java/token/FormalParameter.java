package token;

import java.util.ArrayList;
import visitor.Visitee;
import visitor.Visitor;

public class FormalParameter extends Token implements Visitee {

  public ArrayList<Token> children;

  public FormalParameter(ArrayList<Token> children) {
    super("", TokenType.FormalParameter);
  }

  public void accept(Visitor v) {
    v.visit(this);
  }
}
