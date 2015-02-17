package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class ImportDeclaration extends Token {
  public Name importName;
  public boolean onDemand = false;

  public ImportDeclaration(ArrayList<Token> children) {
    super("", TokenType.ImportDeclaration, children);
    importName = (Name) children.get(1);
    onDemand = children.get(0) instanceof TypeImportOnDemandDeclaration;
  }

  public void accept(Visitor v) throws VisitorException {
    v.visit(this);
  }
}
