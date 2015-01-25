package token;

import java.util.ArrayList;
import visitor.Visitor;

public class SingleTypeImportDeclaration extends Token {

  public ArrayList<Token> children;

  public SingleTypeImportDeclaration(ArrayList<Token> children) {
    super("", TokenType.SingleTypeImportDeclaration);
    this.children = children;
  }

  public void accept(Visitor v) {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
