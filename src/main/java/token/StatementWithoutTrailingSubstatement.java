package token;

import java.util.ArrayList;
import visitor.Visitee;
import visitor.Visitor;

public class StatementWithoutTrailingSubstatement extends Token implements Visitee {

  public ArrayList<Token> children;

  public StatementWithoutTrailingSubstatement(ArrayList<Token> children) {
    super("", TokenType.StatementWithoutTrailingSubstatement);
  }

  public void accept(Visitor v) {
    v.visit(this);
  }
}
