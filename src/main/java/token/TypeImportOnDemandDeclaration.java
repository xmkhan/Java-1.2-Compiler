package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class TypeImportOnDemandDeclaration extends Token {

  public TypeImportOnDemandDeclaration(ArrayList<Token> children) {
    super(children.get(1).getLexeme(), TokenType.TypeImportOnDemandDeclaration, children);
  }

  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
