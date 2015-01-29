package token;

import java.util.ArrayList;
import visitor.Visitor;
import visitor.VisitorException;

public class Type extends Token {

  public ArrayList<Token> children;

  public Type(ArrayList<Token> children) {
    super("", TokenType.Type);
    this.children = children;
  }

  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
