package token;

import java.util.ArrayList;
import visitor.Visitor;
import exception.VisitorException;

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
