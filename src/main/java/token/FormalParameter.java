package token;

import java.util.ArrayList;
import visitor.Visitor;
import visitor.VisitorException;

public class FormalParameter extends Token {

  public FormalParameter(ArrayList<Token> children) {
    super("", TokenType.FormalParameter, children);
  }

  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
