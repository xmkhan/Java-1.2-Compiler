package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class FieldDeclaration extends Declaration {

  public Modifiers modifiers;
  public Type type;
  public Expression expr;

  public FieldDeclaration(ArrayList<Token> children) {
    super("", TokenType.FieldDeclaration, children);
    for (Token token : children) {
      assignType(token);
    }
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

  public void accept(Visitor v) throws VisitorException {
    v.visit(modifiers);
    if (expr != null) v.visit(expr);
    v.visit(this);
  }
}
