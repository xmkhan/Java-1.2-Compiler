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
      types.add((InterfaceType) children.get(2));
      types.addAll(childList.types);
    } else {
      types.add((InterfaceType) children.get(0));
    }
  }

  @Override
  public void accept(Visitor v) throws VisitorException {
    for (InterfaceType type : types) {
      type.accept(v);
    }
    v.visit(this);
  }

  @Override
  public void acceptReverse(Visitor v) throws VisitorException {
    v.visit(this);
    for (InterfaceType type : types) {
      type.acceptReverse(v);
    }
  }
}
