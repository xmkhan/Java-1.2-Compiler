package token;

import java.util.ArrayList;
import visitor.Visitor;
import visitor.VisitorException;

public class LeftHandSide extends Token {

  public LeftHandSide(ArrayList<Token> children) {
    super("", TokenType.LeftHandSide, children);
  }

  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
