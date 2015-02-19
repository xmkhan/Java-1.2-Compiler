package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class ArrayType extends Token {
  private PrimitiveType primitiveType;
  private Name name;

  public ArrayType(ArrayList<Token> children) {
    super("", TokenType.ArrayType, children);
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
    if (token instanceof PrimitiveType) {
      primitiveType = (PrimitiveType) token;
    } else if (token instanceof Name) {
      name = (Name) token;
    }
  }

  public Token getType() {
    if (primitiveType !=  null) return primitiveType.getType();
    else if (name != null) return name.getSimpleName();
    else return null;
  }
}
