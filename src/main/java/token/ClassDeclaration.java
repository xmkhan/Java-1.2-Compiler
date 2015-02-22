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
    lexeme = identifier.getLexeme();
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

  @Override
  public void accept(Visitor v) throws VisitorException {
    if (classBody != null) classBody.accept(v);
    if (modifiers != null) modifiers.accept(v);
    v.visit(this);
  }

  @Override
  public void acceptReverse(Visitor v) throws VisitorException {
    v.visit(this);
    if (classBody != null) classBody.acceptReverse(v);
    if (modifiers != null) modifiers.acceptReverse(v);
  }
}
