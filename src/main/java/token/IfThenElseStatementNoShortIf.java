package token;

import java.util.ArrayList;
import visitor.Visitor;

public class IfThenElseStatementNoShortIf extends Token {

  public ArrayList<Token> children;

  public IfThenElseStatementNoShortIf(ArrayList<Token> children) {
    super("", TokenType.IfThenElseStatementNoShortIf);
    this.children = children;
  }

  public void accept(Visitor v) {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
