package token;

import java.util.ArrayList;
import visitor.Visitor;
import visitor.VisitorException;

public class ArrayAccess extends Token {

  public ArrayList<Token> children;

  public ArrayAccess(ArrayList<Token> children) {
    super("", TokenType.ArrayAccess);
    this.children = children;
  }

  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
