package token;

import exception.VisitorException;
import visitor.TypeCheckToken;
import visitor.Visitor;

import java.util.ArrayList;

public class AdditiveExpression extends Token {
  private final String STRING_CLASS_PATH = "java.lang.String";

  public AdditiveExpression leftExpr;
  public MultiplicativeExpression rightExpr;

  public TypeCheckToken leftType;
  public TypeCheckToken rightType;

  public AdditiveExpression(ArrayList<Token> children) {
    super("", TokenType.AdditiveExpression, children);

    for(Token token : children) {
      assignType(token);
    }
  }

  private void assignType(Token token) {
    if (token instanceof AdditiveExpression) {
      leftExpr = (AdditiveExpression) token;
    } else if (token instanceof MultiplicativeExpression) {
      rightExpr = (MultiplicativeExpression) token;
    }
  }

  public boolean isDefined() {
    return leftExpr != null && rightExpr != null;
  }

  public boolean isAdd() {
    assert(isDefined());
    return children.get(1).getTokenType() == TokenType.PLUS_OP;
  }

  public void setLeftType(TypeCheckToken type) {
    leftType = type;
  }

  public void setRightType(TypeCheckToken type) {
    rightType = type;
  }

  public boolean isLeftString() {
    return !leftType.isArray && leftType.tokenType == TokenType.OBJECT && leftType.getAbsolutePath().equals(STRING_CLASS_PATH);
  }

  public boolean isRightString() {
    return !rightType.isArray && rightType.tokenType == TokenType.OBJECT && rightType.getAbsolutePath().equals(STRING_CLASS_PATH);
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
