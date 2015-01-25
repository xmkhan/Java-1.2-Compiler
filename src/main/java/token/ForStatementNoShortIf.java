package token;

import java.util.ArrayList;
import visitor.Visitee;
import visitor.Visitor;

public class ForStatementNoShortIf extends Token implements Visitee {

  public ArrayList<Token> children;

  public ForStatementNoShortIf(ArrayList<Token> children) {
    super("", TokenType.ForStatementNoShortIf);
  }

  public void accept(Visitor v) {
    v.visit(this);
  }
}
