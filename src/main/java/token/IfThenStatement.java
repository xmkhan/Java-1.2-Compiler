package token;

import java.util.ArrayList;
import visitor.Visitor;
import exception.VisitorException;

public class IfThenStatement extends Token {

  public IfThenStatement(ArrayList<Token> children) {
    super("", TokenType.IfThenStatement, children);
  }

  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
