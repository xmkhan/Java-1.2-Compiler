package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class CharLiteral extends Token {

  public CharLiteral(ArrayList<Token> children) {
    super(children.get(0).getLexeme(), TokenType.CHAR_LITERAL, children);
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
