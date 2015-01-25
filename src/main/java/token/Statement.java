package token;

import java.util.ArrayList;
import visitor.Visitor;

public class Statement extends Token {

  public ArrayList<Token> children;

  public Statement(ArrayList<Token> children) {
    super("", TokenType.Statement);
    this.children = children;
  }

  public void accept(Visitor v) {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
