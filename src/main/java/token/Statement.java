package token;

import java.util.ArrayList;
import visitor.Visitee;
import visitor.Visitor;

public class Statement extends Token implements Visitee {

  public ArrayList<Token> children;

  public Statement(ArrayList<Token> children) {
    super("", TokenType.Statement);
  }

  public void accept(Visitor v) {
    v.visit(this);
  }
}
