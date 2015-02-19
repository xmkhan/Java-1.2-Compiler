package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class InterfaceBody extends Token {

  public InterfaceMemberDeclarations interfaceMemberDeclarations;

  public InterfaceBody(ArrayList<Token> children) {
    super("", TokenType.InterfaceBody, children);
    if (children.get(1) instanceof InterfaceMemberDeclarations) {
      interfaceMemberDeclarations = (InterfaceMemberDeclarations) children.get(1);
    }
  }

  @Override
  public void accept(Visitor v) throws VisitorException {
    if (interfaceMemberDeclarations != null) interfaceMemberDeclarations.accept(v);
    v.visit(this);
  }

  @Override
  public void acceptReverse(Visitor v) throws VisitorException {
    v.visit(this);
    if (interfaceMemberDeclarations != null) interfaceMemberDeclarations.acceptReverse(v);
  }
}
