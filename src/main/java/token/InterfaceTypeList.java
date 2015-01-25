package token;

import java.util.ArrayList;
import visitor.Visitee;
import visitor.Visitor;

public class InterfaceTypeList extends Token implements Visitee {

  public ArrayList<Token> children;

  public InterfaceTypeList(ArrayList<Token> children) {
    super("", TokenType.InterfaceTypeList);
  }

  public void accept(Visitor v) {
    v.visit(this);
  }
}
