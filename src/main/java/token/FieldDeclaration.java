package token;

import java.util.ArrayList;
import visitor.Visitor;

public class FieldDeclaration extends Token {

  public Modifiers modifiers;
  public Type type;
  public VariableDeclarator variableDeclarator;

  public FieldDeclaration(ArrayList<Token> children) {
    super("", TokenType.FieldDeclaration);
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
      variableDeclarator = (VariableDeclarator) token;
    }
  }

  public void accept(Visitor v) {
    v.visit(variableDeclarator);
    v.visit(this);
  }
}
