package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class BooleanLiteral extends Token {

  public BooleanLiteral(ArrayList<Token> children) {
    super(children.get(0).getLexeme(), TokenType.BooleanLiteral, children);
  }

  @Override
  public void accept(Visitor v) throws VisitorException {
    v.visit(this);
  }

  @Override
  public void acceptReverse(Visitor v) throws VisitorException {
    v.visit(this);
  }

  @Override
  public void traverse(Visitor v) throws VisitorException {
    v.visit(this);
  }
}
