package token;

import java.util.ArrayList;
import visitor.Visitee;
import visitor.Visitor;

public class ReturnStatement extends Token implements Visitee {

  public ArrayList<Token> children;

  public ReturnStatement(ArrayList<Token> children) {
    super("", TokenType.ReturnStatement);
  }

  public void accept(Visitor v) {
    v.visit(this);
  }
}
