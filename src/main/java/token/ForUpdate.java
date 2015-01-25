package token;

import java.util.ArrayList;
import visitor.Visitor;

public class ForUpdate extends Token {

  public ArrayList<Token> children;

  public ForUpdate(ArrayList<Token> children) {
    super("", TokenType.ForUpdate);
    this.children = children;
  }

  public void accept(Visitor v) {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
