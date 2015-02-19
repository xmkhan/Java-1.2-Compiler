package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;
import java.util.List;

public class ExtendsInterfaces extends Token {
  private ArrayList<InterfaceType> interfaceTypes;

  public ExtendsInterfaces(ArrayList<Token> children) {
    super("", TokenType.ExtendsInterfaces, children);
    interfaceTypes = new ArrayList<InterfaceType>();
    if (children.get(0) instanceof ExtendsInterfaces) {
      ExtendsInterfaces childInterfaces = (ExtendsInterfaces) children.get(0);
      interfaceTypes.addAll(childInterfaces.interfaceTypes);
      interfaceTypes.add((InterfaceType) children.get(2));
    } else {
      lexeme = children.get(1).getLexeme();
      interfaceTypes.add((InterfaceType) children.get(1));
    }
  }

  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }

  public List<InterfaceType> getInterfaceType() {
    return interfaceTypes;
  }
}
