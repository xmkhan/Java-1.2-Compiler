package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class SingleTypeImportDeclaration extends Token {

  public SingleTypeImportDeclaration(ArrayList<Token> children) {
    super(children.get(1).getLexeme(), TokenType.SingleTypeImportDeclaration, children);
  }

  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
