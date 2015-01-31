package token;

import java.util.ArrayList;
import visitor.Visitor;
import visitor.VisitorException;

public class Interfaces extends Token {
  public InterfaceTypeList interfaceTypeList;

  public Interfaces(ArrayList<Token> children) {
    super("", TokenType.Interfaces, children);
    interfaceTypeList = (InterfaceTypeList) children.get(1);
  }

  public void accept(Visitor v) throws VisitorException {
    if (interfaceTypeList != null) v.visit(interfaceTypeList);
    v.visit(this);
  }
}
