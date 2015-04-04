package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class LocalVariableDeclaration extends Declaration {

  public int offset = -1;
  public Expression expression;

  public LocalVariableDeclaration(ArrayList<Token> children) {
    super("", TokenType.LocalVariableDeclaration, children);
    type = (Type) children.get(0);
    identifier = children.get(1);
    expression = (Expression) children.get(3);
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
