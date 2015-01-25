package token;

import java.util.ArrayList;
import visitor.Visitor;

public class ImportDeclaration extends Token {

  public ArrayList<Token> children;

  public ImportDeclaration(ArrayList<Token> children) {
    super("", TokenType.ImportDeclaration);
    this.children = children;
  }

  public void accept(Visitor v) {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
