package token;

import java.util.ArrayList;
import visitor.Visitor;

public class TypeImportOnDemandDeclaration extends Token {

  public ArrayList<Token> children;

  public TypeImportOnDemandDeclaration(ArrayList<Token> children) {
    super("", TokenType.TypeImportOnDemandDeclaration);
    this.children = children;
  }

  public void accept(Visitor v) {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
