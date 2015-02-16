package token;

import java.util.ArrayList;
import visitor.Visitor;
import exception.VisitorException;

public class SingleTypeImportDeclaration extends Token {

  public SingleTypeImportDeclaration(ArrayList<Token> children) {
    super("", TokenType.SingleTypeImportDeclaration, children);
  }

  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
