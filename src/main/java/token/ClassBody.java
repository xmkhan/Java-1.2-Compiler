package token;

import java.util.ArrayList;
import visitor.Visitee;
import visitor.Visitor;

public class ClassBody extends Token implements Visitee {

  public ArrayList<Token> children;

  public ClassBody(ArrayList<Token> children) {
    super("", TokenType.ClassBody);
  }

  public void accept(Visitor v) {
    v.visit(this);
  }
}
