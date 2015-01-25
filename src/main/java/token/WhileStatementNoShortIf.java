package token;

import java.util.ArrayList;
import visitor.Visitor;

public class WhileStatementNoShortIf extends Token {

  public ArrayList<Token> children;

  public WhileStatementNoShortIf(ArrayList<Token> children) {
    super("", TokenType.WhileStatementNoShortIf);
    this.children = children;
  }

  public void accept(Visitor v) {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
