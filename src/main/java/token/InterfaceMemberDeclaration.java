package token;

import java.util.ArrayList;
import visitor.Visitor;

public class InterfaceMemberDeclaration extends Token {

  public ArrayList<Token> children;

  public InterfaceMemberDeclaration(ArrayList<Token> children) {
    super("", TokenType.InterfaceMemberDeclaration);
    this.children = children;
  }

  public void accept(Visitor v) {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
