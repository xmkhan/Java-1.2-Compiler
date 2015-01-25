package token;

import java.util.ArrayList;
import visitor.Visitee;
import visitor.Visitor;

public class IfThenStatement extends Token implements Visitee {

  public ArrayList<Token> children;

  public IfThenStatement(ArrayList<Token> children) {
    super("", TokenType.IfThenStatement);
  }

  public void accept(Visitor v) {
    v.visit(this);
  }
}
