package token;

import java.util.ArrayList;
import visitor.Visitee;
import visitor.Visitor;

public class ForStatement extends Token implements Visitee {

  public ArrayList<Token> children;

  public ForStatement(ArrayList<Token> children) {
    super("", TokenType.ForStatement);
  }

  public void accept(Visitor v) {
    v.visit(this);
  }
}
