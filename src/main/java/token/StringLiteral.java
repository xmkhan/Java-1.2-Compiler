package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class StringLiteral extends Token {

  public StringLiteral(ArrayList<Token> children) {
    super(children.get(0).getLexeme(), TokenType.STR_LITERAL, children);
  }

  public void accept(Visitor v) throws VisitorException {
    v.visit(this);
  }
}
