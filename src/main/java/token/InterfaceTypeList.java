package token;

import java.util.ArrayList;
import visitor.Visitor;

public class InterfaceTypeList extends Token {

  public ArrayList<Token> children;

  public InterfaceTypeList(ArrayList<Token> children) {
    super("", TokenType.InterfaceTypeList);
    this.children = children;
  }

  public void accept(Visitor v) {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
