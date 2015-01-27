package token;

import java.util.ArrayList;
import visitor.Visitor;

public class InterfaceDeclaration extends Token {

  public Modifiers modifiers;
  public Token identifier;
  public ExtendsInterfaces extendsInterfaces;
  public InterfaceBody interfaceBody;

  public InterfaceDeclaration(ArrayList<Token> children) {
    super("", TokenType.InterfaceDeclaration);
    for (Token token : children) {
      assignType(token);
    }
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

  public void accept(Visitor v) {
    v.visit(interfaceBody);
    v.visit(this);
  }
}
