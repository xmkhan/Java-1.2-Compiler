package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class PackageDeclaration extends Token {

  public PackageDeclaration(ArrayList<Token> children) {
    super("", TokenType.PackageDeclaration, children);
  }

  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
