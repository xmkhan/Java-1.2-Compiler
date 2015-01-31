package token;

import java.util.ArrayList;
import visitor.Visitor;
import visitor.VisitorException;

public class MethodBody extends Token {

  public MethodBody(ArrayList<Token> children) {
    super("", TokenType.MethodBody, children);
  }

  public boolean isEmpty() {
    return children.get(0).getTokenType() == TokenType.SEMI_COLON;
  }

  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
