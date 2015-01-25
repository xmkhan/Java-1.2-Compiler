package token;

import java.util.ArrayList;
import visitor.Visitee;
import visitor.Visitor;

public class PackageDeclaration extends Token implements Visitee {

  public ArrayList<Token> children;

  public PackageDeclaration(ArrayList<Token> children) {
    super("", TokenType.PackageDeclaration);
  }

  public void accept(Visitor v) {
    v.visit(this);
  }
}
