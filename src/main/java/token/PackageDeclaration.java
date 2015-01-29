package token;

import java.util.ArrayList;
import visitor.Visitor;
import visitor.VisitorException;

public class PackageDeclaration extends Token {

  public ArrayList<Token> children;

  public PackageDeclaration(ArrayList<Token> children) {
    super("", TokenType.PackageDeclaration);
    this.children = children;
  }

  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
