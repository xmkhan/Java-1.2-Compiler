package token;

import java.util.ArrayList;
import visitor.Visitor;
import visitor.VisitorException;

public class InterfaceBody extends Token {

  public ArrayList<Token> children;

  public InterfaceBody(ArrayList<Token> children) {
    super("", TokenType.InterfaceBody);
    this.children = children;
  }

  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
