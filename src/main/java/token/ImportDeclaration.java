package token;

import java.util.ArrayList;
import visitor.Visitee;
import visitor.Visitor;

public class ImportDeclaration extends Token implements Visitee {

  public ArrayList<Token> children;

  public ImportDeclaration(ArrayList<Token> children) {
    super("", TokenType.ImportDeclaration);
  }

  public void accept(Visitor v) {
    v.visit(this);
  }
}
