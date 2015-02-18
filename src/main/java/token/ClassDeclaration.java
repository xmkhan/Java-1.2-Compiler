package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class ClassDeclaration extends Declaration {

  public Modifiers modifiers;
  public Super extendsClass;
  public Interfaces implementsClasses;
  public ClassBody classBody;

  public ClassDeclaration(ArrayList<Token> children) {
    super("", TokenType.ClassDeclaration, children);
    for (Token token : children) {
      assignType(token);
    }
  }

  private void assignType(Token token) {
    if (token instanceof Modifiers) {
      modifiers = (Modifiers) token;
    } else if (token.getTokenType() == TokenType.IDENTIFIER) {
      identifier = token;
    } else if (token instanceof Super) {
      extendsClass = (Super) token;
    } else if (token instanceof Interfaces) {
      implementsClasses = (Interfaces) token;
    } else if (token instanceof ClassBody) {
      classBody = (ClassBody) token;
    }
  }

  public void accept(Visitor v) throws VisitorException {
    classBody.accept(v);
    modifiers.accept(v);
    v.visit(this);
  }
}
