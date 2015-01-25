package token;

import java.util.ArrayList;
import visitor.Visitor;

public class InterfaceMemberDeclarations extends Token {

  public ArrayList<Token> children;

  public InterfaceMemberDeclarations(ArrayList<Token> children) {
    super("", TokenType.InterfaceMemberDeclarations);
    this.children = children;
  }

  public void accept(Visitor v) {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
