package token;

import java.util.ArrayList;
import visitor.Visitee;
import visitor.Visitor;

public class ArrayType extends Token implements Visitee {

  public ArrayList<Token> children;

  public ArrayType(ArrayList<Token> children) {
    super("", TokenType.ArrayType);
  }

  public void accept(Visitor v) {
    v.visit(this);
  }
}
