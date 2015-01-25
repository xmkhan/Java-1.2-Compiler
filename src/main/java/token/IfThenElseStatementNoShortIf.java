package token;

import java.util.ArrayList;
import visitor.Visitee;
import visitor.Visitor;

public class IfThenElseStatementNoShortIf extends Token implements Visitee {

  public ArrayList<Token> children;

  public IfThenElseStatementNoShortIf(ArrayList<Token> children) {
    super("", TokenType.IfThenElseStatementNoShortIf);
  }

  public void accept(Visitor v) {
    v.visit(this);
  }
}
