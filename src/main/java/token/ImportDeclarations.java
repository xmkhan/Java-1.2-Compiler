package token;

import java.util.ArrayList;
import visitor.Visitee;
import visitor.Visitor;

public class ImportDeclarations extends Token implements Visitee {

  public ArrayList<Token> children;

  public ImportDeclarations(ArrayList<Token> children) {
    super("", TokenType.ImportDeclarations);
  }

  public void accept(Visitor v) {
    v.visit(this);
  }
}
