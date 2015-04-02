package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class FieldDeclaration extends Declaration {

  public Modifiers modifiers;
  public Expression expr;
  public Token delimiter;

  public int offset = -1;

  public FieldDeclaration(ArrayList<Token> children) {
    super("", TokenType.FieldDeclaration, children);
    for (Token token : children) {
      assignType(token);
    }
    delimiter = children.get(children.size() - 1);
  }

  public boolean containsModifier(String modifier) {
    return modifiers != null && modifiers.containsModifier(modifier);
  }

  private void assignType(Token token) {
    if (token instanceof Modifiers) {
      modifiers = (Modifiers) token;
    } else if (token instanceof Type) {
      type = (Type) token;
    } else if (token instanceof VariableDeclarator) {
      identifier = token.children.get(0);
      if (token.children.size() == 3) {
        expr = (Expression) token.children.get(2);
      }
    }
  }

  @Override
  public void accept(Visitor v) throws VisitorException {
    v.visit(modifiers);
    if (type != null) type.accept(v);
    if (expr != null) expr.accept(v);
    v.visit(delimiter);
    v.visit(this);
  }

  @Override
  public void acceptReverse(Visitor v) throws VisitorException {
    v.visit(this);
    v.visit(modifiers);
    if (type != null) type.acceptReverse(v);
    if (expr != null) expr.acceptReverse(v);
    v.visit(delimiter);
  }

  @Override
  public void traverse(Visitor v) throws VisitorException {
    v.visit(this);
  }
}
