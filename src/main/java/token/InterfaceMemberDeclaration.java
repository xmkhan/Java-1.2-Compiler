package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class InterfaceMemberDeclaration extends Token {
  AbstractMethodDeclaration abstractMethodDeclaration;

  public InterfaceMemberDeclaration(ArrayList<Token> children) {
    super("", TokenType.InterfaceMemberDeclaration, children);
    abstractMethodDeclaration = (AbstractMethodDeclaration) children.get(0);
  }

  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }

  public MethodHeader getMethodHeader() {
    return (MethodHeader) abstractMethodDeclaration.children.get(0);
  }
}
