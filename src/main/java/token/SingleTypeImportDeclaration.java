package token;

import java.util.ArrayList;
import visitor.Visitee;
import visitor.Visitor;

public class SingleTypeImportDeclaration extends Token implements Visitee {

  public ArrayList<Token> children;

  public SingleTypeImportDeclaration(ArrayList<Token> children) {
    super("", TokenType.SingleTypeImportDeclaration);
  }

  public void accept(Visitor v) {
    v.visit(this);
  }
}
