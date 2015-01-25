package token;

import java.util.ArrayList;
import visitor.Visitee;
import visitor.Visitor;

public class StatementNoShortIf extends Token implements Visitee {

  public ArrayList<Token> children;

  public StatementNoShortIf(ArrayList<Token> children) {
    super("", TokenType.StatementNoShortIf);
  }

  public void accept(Visitor v) {
    v.visit(this);
  }
}
