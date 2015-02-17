package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class ImportDeclarations extends Token {

  public ArrayList<ImportDeclaration> importDeclarations;

  public ImportDeclarations(ArrayList<Token> children) {
    super("", TokenType.ImportDeclarations, children);
    importDeclarations = new ArrayList<ImportDeclaration>();
    if (children.get(0) instanceof ImportDeclarations) {
      importDeclarations.addAll(((ImportDeclarations) children.get(0)).importDeclarations);
      importDeclarations.add((ImportDeclaration)children.get(1));
    } else {
      importDeclarations.add((ImportDeclaration)children.get(0));
    }
  }

  public void accept(Visitor v) throws VisitorException {
    v.visit(this);
  }
}
