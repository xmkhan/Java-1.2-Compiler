package token;

import java.util.ArrayList;
import visitor.Visitor;

public class ClassDeclaration extends Token {

  public Modifiers modifiers;
  public Token identifier;
  public Super extendsClass;
  public Interfaces implementsClasses;
  public ClassBody classBody;

  public ClassDeclaration(ArrayList<Token> children) {
    super("", TokenType.ClassDeclaration);
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

  public void accept(Visitor v) {
    v.visit(classBody);
    v.visit(this);
  }
}
