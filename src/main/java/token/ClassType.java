package token;

import java.util.ArrayList;
import visitor.Visitee;
import visitor.Visitor;

public class ClassType extends Token implements Visitee {

  public ArrayList<Token> children;

  public ClassType(ArrayList<Token> children) {
    super("", TokenType.ClassType);
  }

  public void accept(Visitor v) {
    v.visit(this);
  }
}
