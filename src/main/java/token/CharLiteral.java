package token;

import java.util.ArrayList;
import visitor.Visitor;

public class CharLiteral extends Token {

  public ArrayList<Token> children;

  public CharLiteral(ArrayList<Token> children) {
    super("", TokenType.CHAR_LITERAL);
    this.children = children;
  }

  public void accept(Visitor v) {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
