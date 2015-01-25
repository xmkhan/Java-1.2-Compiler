package token;

import java.util.ArrayList;
import visitor.Visitee;
import visitor.Visitor;

public class ClassOrInterfaceType extends Token implements Visitee {

  public ArrayList<Token> children;

  public ClassOrInterfaceType(ArrayList<Token> children) {
    super("", TokenType.ClassOrInterfaceType);
  }

  public void accept(Visitor v) {
    v.visit(this);
  }
}
