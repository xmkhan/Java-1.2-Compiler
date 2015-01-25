package token;

import java.util.ArrayList;
import visitor.Visitor;

public class IfThenStatement extends Token {

  public ArrayList<Token> children;

  public IfThenStatement(ArrayList<Token> children) {
    super("", TokenType.IfThenStatement);
    this.children = children;
  }

  public void accept(Visitor v) {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
