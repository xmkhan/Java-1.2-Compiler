package token;

import java.util.ArrayList;
import visitor.Visitor;
import exception.VisitorException;

public class EmptyStatement extends Token {

  public EmptyStatement(ArrayList<Token> children) {
    super("", TokenType.EmptyStatement, children);
  }

  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
