package token;

import java.util.ArrayList;
import visitor.Visitee;
import visitor.Visitor;

public class WhileStatementNoShortIf extends Token implements Visitee {

  public ArrayList<Token> children;

  public WhileStatementNoShortIf(ArrayList<Token> children) {
    super("", TokenType.WhileStatementNoShortIf);
  }

  public void accept(Visitor v) {
    v.visit(this);
  }
}
