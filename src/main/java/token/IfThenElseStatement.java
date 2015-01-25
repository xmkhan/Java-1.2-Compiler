package token;

import java.util.ArrayList;
import visitor.Visitee;
import visitor.Visitor;

public class IfThenElseStatement extends Token implements Visitee {

  public ArrayList<Token> children;

  public IfThenElseStatement(ArrayList<Token> children) {
    super("", TokenType.IfThenElseStatement);
  }

  public void accept(Visitor v) {
    v.visit(this);
  }
}
