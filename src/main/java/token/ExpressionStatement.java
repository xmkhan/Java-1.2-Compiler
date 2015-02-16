package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class ExpressionStatement extends Token {

  public ExpressionStatement(ArrayList<Token> children) {
    super("", TokenType.ExpressionStatement, children);
  }

  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
