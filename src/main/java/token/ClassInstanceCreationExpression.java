package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class ClassInstanceCreationExpression extends Token {
  public ClassType classType;
  public ArgumentList argumentList;

  public ClassInstanceCreationExpression(ArrayList<Token> children) {
    super("", TokenType.ClassInstanceCreationExpression, children);
    for (Token token : children) {
      assignType(token);
    }
  }

  private void assignType(Token token) {
    if (token instanceof ClassType) {
      classType = (ClassType) token;
    } else if (token instanceof ArgumentList) {
      argumentList = (ArgumentList) token;
    }
  }

  public Name getClassType() {
    return classType.classOrInterfaceType.name;
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
}
