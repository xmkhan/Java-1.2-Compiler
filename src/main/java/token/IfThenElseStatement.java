package token;

import java.util.ArrayList;
import visitor.Visitor;

public class IfThenElseStatement extends Token {

  public ArrayList<Token> children;

  public IfThenElseStatement(ArrayList<Token> children) {
    super("", TokenType.IfThenElseStatement);
    this.children = children;
  }

  public void accept(Visitor v) {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
