package token;

import exception.VisitorException;
import visitor.TypeCheckToken;
import visitor.Visitor;

import java.util.ArrayList;

public class Primary extends Token {

  private TypeCheckToken determinedType;

  public Primary(ArrayList<Token> children) {
    super("", TokenType.Primary, children);
  }

  public TypeCheckToken getDeterminedType() {
    return determinedType;
  }

  public void setDeterminedType(TypeCheckToken determinedType) {
    this.determinedType = determinedType;
  }

  @Override
  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }

  @Override
  public void acceptReverse(Visitor v) throws VisitorException {
    v.visit(this);
    for (Token token : children) {
      token.acceptReverse(v);
    }
  }

  @Override
  public void traverse(Visitor v) throws VisitorException {
    v.visit(this);
  }
}
