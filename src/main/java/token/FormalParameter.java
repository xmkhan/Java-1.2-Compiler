package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class FormalParameter extends Declaration {

  public int offset = -1;

  public FormalParameter(ArrayList<Token> children) {
    super("", TokenType.FormalParameter, children);
    type = (Type) children.get(0);
    identifier = children.get(1);
  }

  @Override
  public void accept(Visitor v) throws VisitorException {
    type.accept(v);
    v.visit(this);
  }

  @Override
  public void acceptReverse(Visitor v) throws VisitorException {
    v.visit(this);
    type.acceptReverse(v);
  }

  private void assignType(Token token) {
    if (token instanceof Type) {
      type = (Type) token;
    } else if (token.getTokenType().equals(TokenType.IDENTIFIER)) {
      identifier = token;
    }
  }

  @Override
  public void traverse(Visitor v) throws VisitorException {
    v.visit(this);
  }

  public boolean isPrimitive() {
    return type.isPrimitiveType();
  }

  public boolean isReferenceType() {
    return type.isReferenceType();
  }

  public boolean isArray() {
    return type != null ? type.isArray() : false;
  }

  public Token getType() {
    return type != null ? type.getType() : null;
  }

  public String getTypeString() {
    String prefix;
    if (type.isPrimitiveType()) {
      prefix = getType().getLexeme();
    } else {
      prefix = type.referenceType.getReferenceName().getAbsolutePath();
    }
    return isArray() ? prefix + "[]" : prefix;
  }
}
