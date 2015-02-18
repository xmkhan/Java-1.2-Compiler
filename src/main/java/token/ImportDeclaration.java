package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class ImportDeclaration extends Token {
  public boolean onDemand = false;

  public ImportDeclaration(ArrayList<Token> children) {
    super(children.get(0).getLexeme(), TokenType.ImportDeclaration, children);
    onDemand = children.get(0) instanceof TypeImportOnDemandDeclaration;
  }

  public void accept(Visitor v) throws VisitorException {
    v.visit(this);
  }
}
