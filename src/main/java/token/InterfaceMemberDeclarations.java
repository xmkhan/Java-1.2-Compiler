package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class InterfaceMemberDeclarations extends Token {
  private ArrayList<InterfaceMemberDeclaration> interfaceMemberDeclarations;

  public ArrayList<InterfaceMemberDeclaration> getInterfaceMemberDeclarations() {
    return interfaceMemberDeclarations;
  }

  public InterfaceMemberDeclarations(ArrayList<Token> children) {
    super("", TokenType.InterfaceMemberDeclarations, children);
    interfaceMemberDeclarations = new ArrayList<InterfaceMemberDeclaration>();
    if (children.get(0) instanceof InterfaceMemberDeclaration) {
      interfaceMemberDeclarations.add((InterfaceMemberDeclaration) children.get(0));
    } else {
      InterfaceMemberDeclarations childDeclarations = (InterfaceMemberDeclarations) children.get(0);
      interfaceMemberDeclarations.addAll(childDeclarations.interfaceMemberDeclarations);
      interfaceMemberDeclarations.add((InterfaceMemberDeclaration) children.get(1));
    }
  }

  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
