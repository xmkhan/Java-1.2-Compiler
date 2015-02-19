package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class FormalParameter extends Token {
  private Type type;
  private Token identifier;

  public FormalParameter(ArrayList<Token> children) {
    super("", TokenType.FormalParameter, children);
    for (Token token : children) {
      assignType(token);
    }
  }

  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }

  private void assignType(Token token) {
    if (token instanceof Type) {
      type = (Type) token;
    } else if (token.getTokenType().equals(TokenType.IDENTIFIER)) {
      identifier = token;
    }
  }

  public boolean isPrimitive() {
    return children.get(0).children.get(0).getTokenType().equals(TokenType.PrimitiveType);
  }

  public boolean isReferenceType() {
    return children.get(0).getTokenType().equals(TokenType.ReferenceType);
  }

  public boolean isArray() {
    return type != null ? type.isArray() : false;
  }

  public Token getType() {
    return type != null ? type.getType() : null;
  }
}
