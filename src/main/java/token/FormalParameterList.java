package token;

import java.util.ArrayList;
import visitor.Visitor;

public class FormalParameterList extends Token {

  public ArrayList<Token> children;

  public FormalParameterList(ArrayList<Token> children) {
    super("", TokenType.FormalParameterList);
    this.children = children;
  }

  public void accept(Visitor v) {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
