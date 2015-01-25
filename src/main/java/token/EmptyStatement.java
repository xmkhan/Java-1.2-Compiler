package token;

import java.util.ArrayList;
import visitor.Visitee;
import visitor.Visitor;

public class EmptyStatement extends Token implements Visitee {

  public ArrayList<Token> children;

  public EmptyStatement(ArrayList<Token> children) {
    super("", TokenType.EmptyStatement);
  }

  public void accept(Visitor v) {
    v.visit(this);
  }
}
