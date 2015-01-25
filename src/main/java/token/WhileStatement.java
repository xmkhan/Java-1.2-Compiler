package token;

import java.util.ArrayList;
import visitor.Visitee;
import visitor.Visitor;

public class WhileStatement extends Token implements Visitee {

  public ArrayList<Token> children;

  public WhileStatement(ArrayList<Token> children) {
    super("", TokenType.WhileStatement);
  }

  public void accept(Visitor v) {
    v.visit(this);
  }
}
