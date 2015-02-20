package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class BooleanLiteral extends Token {

  public BooleanLiteral(ArrayList<Token> children) {
    super(children.get(0).getLexeme(), TokenType.BooleanLiteral, children);
  }

  public void accept(Visitor v) throws VisitorException {
    v.visit(this);
  }

  public void acceptReverse(Visitor v) throws VisitorException {
    v.visit(this);
  }
}
