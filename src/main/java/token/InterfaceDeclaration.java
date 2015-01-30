package token;

import java.util.ArrayList;
import visitor.Visitor;
import visitor.VisitorException;

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

  public void accept(Visitor v) throws VisitorException {
    interfaceBody.accept(v);
    v.visit(modifiers);
    v.visit(this);
  }
}
