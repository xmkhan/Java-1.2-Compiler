package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class IntLiteral extends Token {

  public IntLiteral(ArrayList<Token> children) {
    super(children.get(0).getLexeme(), TokenType.INT_LITERAL, children);
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
