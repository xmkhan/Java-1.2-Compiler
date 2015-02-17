package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class PackageDeclaration extends Token {
  public Name name;


  public PackageDeclaration(ArrayList<Token> children) {
    super("", TokenType.PackageDeclaration, children);
    name = (Name) children.get(1);
  }

  public void accept(Visitor v) throws VisitorException {
    v.visit(this);
  }
}
