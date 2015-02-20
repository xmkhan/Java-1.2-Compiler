package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class Interfaces extends Token {
  public InterfaceTypeList interfaceTypeList;

  public Interfaces(ArrayList<Token> children) {
    super("", TokenType.Interfaces, children);
    interfaceTypeList = (InterfaceTypeList) children.get(1);
  }

  @Override
  public void accept(Visitor v) throws VisitorException {
    if (interfaceTypeList != null) interfaceTypeList.accept(v);
    v.visit(this);
  }

  @Override
  public void acceptReverse(Visitor v) throws VisitorException {
    v.visit(this);
    if (interfaceTypeList != null) interfaceTypeList.acceptReverse(v);
  }
}
