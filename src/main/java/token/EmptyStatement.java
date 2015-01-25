package token;

import java.util.ArrayList;
import visitor.Visitor;

public class EmptyStatement extends Token {

  public ArrayList<Token> children;

  public EmptyStatement(ArrayList<Token> children) {
    super("", TokenType.EmptyStatement);
    this.children = children;
  }

  public void accept(Visitor v) {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
