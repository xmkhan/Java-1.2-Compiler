package token;

import java.util.ArrayList;
import visitor.Visitee;
import visitor.Visitor;

public class FormalParameterList extends Token implements Visitee {

  public ArrayList<Token> children;

  public FormalParameterList(ArrayList<Token> children) {
    super("", TokenType.FormalParameterList);
  }

  public void accept(Visitor v) {
    v.visit(this);
  }
}
