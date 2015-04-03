package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class ReferenceType extends Token {
  public ArrayType arrayType;
  public ClassOrInterfaceType classOrInterfaceType;

  public ReferenceType(ArrayList<Token> children) {
    super("", TokenType.ReferenceType, children);
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

  @Override
  public void traverse(Visitor v) throws VisitorException {
    v.visit(this);
  }

  private void assignType(Token token) {
    if (token instanceof ArrayType) {
      arrayType = (ArrayType) token;
    } else if (token instanceof ClassOrInterfaceType) {
      classOrInterfaceType = (ClassOrInterfaceType) token;
    }
  }

  public Token getType() {
    if (arrayType !=  null) return arrayType.getType();
    else if (classOrInterfaceType != null) return classOrInterfaceType.getType();
    else return null;
  }

  public boolean isReferenceType() {
    return classOrInterfaceType != null ||
           arrayType != null && arrayType.name != null;
  }

  public boolean isPrimitiveType() {
    return arrayType != null && arrayType.primitiveType != null;
  }

  public Name getReferenceName() {
    if(!isReferenceType()) {
      return null;
    }

    if(classOrInterfaceType != null) {
      return classOrInterfaceType.name;
    } else {
      return arrayType.name;
    }
  }

  public boolean isArray() {
    return arrayType != null;
  }
}
