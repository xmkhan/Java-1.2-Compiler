package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class Type extends Token {
  PrimitiveType primitiveType;
  ReferenceType referenceType;
  Token identifier;

  public Type(ArrayList<Token> children) {
    super("", TokenType.Type, children);
    for (Token token : children) {
      assignType(token);
    }
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

  private void assignType(Token token) {
    if (token instanceof PrimitiveType) {
      primitiveType = (PrimitiveType) token;
    } else if (token.getTokenType() == TokenType.IDENTIFIER) {
      identifier = token;
    } else if (token instanceof ReferenceType) {
      referenceType = (ReferenceType) token;
    }
  }

  public Token getType() {
    if (primitiveType !=  null) return primitiveType.getType();
    else if (referenceType != null) return referenceType.getType();
    else return null;
  }

  public boolean isArray() {
    return referenceType != null ? referenceType.isArray() : false;
  }
}
