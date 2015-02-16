package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class InterfaceTypeList extends Token {

  public ArrayList<InterfaceType> types;

  public InterfaceTypeList(ArrayList<Token> children) {
    super("", TokenType.InterfaceTypeList, children);
    types = new ArrayList<InterfaceType>();
    if (children.get(0) instanceof InterfaceTypeList) {
      InterfaceTypeList childList = (InterfaceTypeList) children.get(0);
      types.addAll(childList.types);
    } else {
      types.add((InterfaceType) children.get(0));
    }
  }

  public void accept(Visitor v) throws VisitorException {
    v.visit(this);
  }
}
