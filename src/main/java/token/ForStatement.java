package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class ForStatement extends Token {

  public ForStatement(ArrayList<Token> children) {
    super("", TokenType.ForStatement, children);
  }

  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
