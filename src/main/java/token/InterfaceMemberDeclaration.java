package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class InterfaceMemberDeclaration extends Token {

  public InterfaceMemberDeclaration(ArrayList<Token> children) {
    super("", TokenType.InterfaceMemberDeclaration, children);
  }

  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
