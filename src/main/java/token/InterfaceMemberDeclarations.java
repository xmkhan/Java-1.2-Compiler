package token;

import java.util.ArrayList;
import visitor.Visitor;
import exception.VisitorException;

public class InterfaceMemberDeclarations extends Token {

  public InterfaceMemberDeclarations(ArrayList<Token> children) {
    super("", TokenType.InterfaceMemberDeclarations, children);
  }

  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
