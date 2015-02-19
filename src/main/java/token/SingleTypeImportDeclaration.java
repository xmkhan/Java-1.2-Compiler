package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class SingleTypeImportDeclaration extends Token {

  public SingleTypeImportDeclaration(ArrayList<Token> children) {
    super(children.get(1).getLexeme(), TokenType.SingleTypeImportDeclaration, children);
  }

  @Override
  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }

  @Override
  public void acceptReverse(Visitor v) throws VisitorException {
    v.visit(this);
    for (Token token : children) {
      token.acceptReverse(v);
    }
  }
}
