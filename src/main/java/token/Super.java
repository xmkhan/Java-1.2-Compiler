package token;

import java.util.ArrayList;
import visitor.Visitor;
import visitor.VisitorException;

public class Super extends Token {

  public ArrayList<Token> children;

  public Super(ArrayList<Token> children) {
    super("", TokenType.Super);
    this.children = children;
  }

  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
