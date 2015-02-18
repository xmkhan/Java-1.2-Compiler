package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class InterfaceMemberDeclarations extends Token {

  public ArrayList<InterfaceMemberDeclaration> memberDeclarations;

  public ArrayList<InterfaceMemberDeclaration> getMemberDeclarations() {
    return memberDeclarations;
  }

  public InterfaceMemberDeclarations(ArrayList<Token> children) {
    super(children.get(0).getLexeme(), TokenType.InterfaceMemberDeclarations, children);
    memberDeclarations = new ArrayList<InterfaceMemberDeclaration>();
    if (children.get(0) instanceof InterfaceMemberDeclaration) {
      memberDeclarations.add((InterfaceMemberDeclaration) children.get(0));
    } else {
      InterfaceMemberDeclarations childMemberDeclarations = (InterfaceMemberDeclarations) children.get(0);
      memberDeclarations.addAll(childMemberDeclarations.memberDeclarations);
      memberDeclarations.add((InterfaceMemberDeclaration) children.get(1));
    }
  }

  public void accept(Visitor v) throws VisitorException {
    for (Token token : memberDeclarations) {
      token.accept(v);
    }
    v.visit(this);
  }
}
