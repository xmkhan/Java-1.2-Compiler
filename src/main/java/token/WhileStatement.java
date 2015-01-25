package token;

import java.util.ArrayList;
import visitor.Visitor;

public class WhileStatement extends Token {

  public ArrayList<Token> children;

  public WhileStatement(ArrayList<Token> children) {
    super("", TokenType.WhileStatement);
    this.children = children;
  }

  public void accept(Visitor v) {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
