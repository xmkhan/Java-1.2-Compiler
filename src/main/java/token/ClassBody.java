package token;

import java.util.ArrayList;
import visitor.Visitor;

public class ClassBody extends Token {

  public ArrayList<Token> children;

  public ClassBody(ArrayList<Token> children) {
    super("", TokenType.ClassBody);
    this.children = children;
  }

  public void accept(Visitor v) {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
