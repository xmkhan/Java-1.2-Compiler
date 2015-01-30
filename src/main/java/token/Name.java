package token;

import java.util.ArrayList;
import visitor.Visitor;
import visitor.VisitorException;

public class Name extends Token {

  public ArrayList<Token> children;

  public Name(ArrayList<Token> children) {
    super("", TokenType.Name);
    this.children = children;
  }

  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
