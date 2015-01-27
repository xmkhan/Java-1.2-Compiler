package token;

import java.util.ArrayList;
import visitor.Visitor;

public class InterfaceTypeList extends Token {

  public ArrayList<InterfaceType> types;

  public InterfaceTypeList(ArrayList<Token> children) {
    super("", TokenType.InterfaceTypeList);
    types = new ArrayList<>();
    if (children.get(0) instanceof InterfaceTypeList) {
      InterfaceTypeList childList = (InterfaceTypeList) children.get(0);
      types.addAll(childList.types);
    } else {
      types.add((InterfaceType) children.get(0));
    }
  }

  public void accept(Visitor v) {
    v.visit(this);
  }
}
