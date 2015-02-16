package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class ReturnStatement extends Token {

  public ReturnStatement(ArrayList<Token> children) {
    super("", TokenType.ReturnStatement, children);
  }

  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
