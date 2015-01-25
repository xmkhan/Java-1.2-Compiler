package token;

import java.util.ArrayList;
import visitor.Visitee;
import visitor.Visitor;

public class ExtendsInterfaces extends Token implements Visitee {

  public ArrayList<Token> children;

  public ExtendsInterfaces(ArrayList<Token> children) {
    super("", TokenType.ExtendsInterfaces);
  }

  public void accept(Visitor v) {
    v.visit(this);
  }
}
