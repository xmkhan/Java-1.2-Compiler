package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class InterfaceDeclaration extends Declaration {

  public Modifiers modifiers;
  public ExtendsInterfaces extendsInterfaces;
  public InterfaceBody interfaceBody;

  public InterfaceDeclaration(ArrayList<Token> children) {
    super("", TokenType.InterfaceDeclaration, children);
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
    } else if (token instanceof ExtendsInterfaces) {
      extendsInterfaces = (ExtendsInterfaces) token;
    } else if (token instanceof InterfaceBody) {
      interfaceBody = (InterfaceBody) token;
    }
  }

  @Override
  public void accept(Visitor v) throws VisitorException {
    if (interfaceBody != null) interfaceBody.accept(v);
    if (modifiers != null) modifiers.accept(v);
    v.visit(this);
  }

  @Override
  public void acceptReverse(Visitor v) throws VisitorException {
    v.visit(this);
    if (interfaceBody != null) interfaceBody.acceptReverse(v);
    if (modifiers != null) modifiers.acceptReverse(v);
  }
}
