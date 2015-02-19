package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class InterfaceBody extends Token {
  private InterfaceMemberDeclarations interfaceMemberDeclarations;

  public InterfaceBody(ArrayList<Token> children) {
    super("", TokenType.InterfaceBody, children);
    this.interfaceMemberDeclarations = (InterfaceMemberDeclarations) children.get(1);
  }

  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }

  public InterfaceMemberDeclarations getInterfaceMemberDeclaration() {
    return interfaceMemberDeclarations;
  }
}
