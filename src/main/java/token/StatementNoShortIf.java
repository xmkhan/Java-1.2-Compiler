package token;

import java.util.ArrayList;
import visitor.Visitor;

public class StatementNoShortIf extends Token {

  public ArrayList<Token> children;

  public StatementNoShortIf(ArrayList<Token> children) {
    super("", TokenType.StatementNoShortIf);
    this.children = children;
  }

  public void accept(Visitor v) {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}