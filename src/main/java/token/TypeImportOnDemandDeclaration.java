package token;

import java.util.ArrayList;
import visitor.Visitor;
import exception.VisitorException;

public class TypeImportOnDemandDeclaration extends Token {

  public TypeImportOnDemandDeclaration(ArrayList<Token> children) {
    super("", TokenType.TypeImportOnDemandDeclaration, children);
  }

  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
