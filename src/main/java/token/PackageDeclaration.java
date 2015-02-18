package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class PackageDeclaration extends Token {

  public PackageDeclaration(ArrayList<Token> children) {
    super(children.get(1).getLexeme(), TokenType.PackageDeclaration, children);
  }

  public void accept(Visitor v) throws VisitorException {
    v.visit(this);
  }
}
