package token;

import com.sun.tools.internal.ws.wsdl.document.Import;
import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;
import java.util.List;

public class ImportDeclarations extends Token {

  public List<ImportDeclaration> importDeclarations;

  public List<ImportDeclaration> getImportDeclarations() {
    return importDeclarations;
  }

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

  /**
   * Returns true if there contains at least ONE import with the matching suffix.
   */
  public boolean containsSuffix(String suffix) {
    for (ImportDeclaration decl : importDeclarations) {
      if (decl.isSingle() && decl.containsSuffix(suffix)) return true;
    }
    return false;
  }

  /**
   * Gets the subset of all imports that match the suffix.
   */
  public List<ImportDeclaration> getAllImportsWithSuffix(String suffix) {
    List<ImportDeclaration> decls = new ArrayList<ImportDeclaration>();
    for (ImportDeclaration decl : importDeclarations) {
      if (decl.containsSuffix(suffix)) decls.add(decl);
    }
    return decls;
  }

  public List<ImportDeclaration> getAllOnDemandImports() {
    List<ImportDeclaration> decls = new ArrayList<ImportDeclaration>();
    for (ImportDeclaration decl : importDeclarations) {
      if (decl.isOnDemand()) decls.add(decl);
    }
    return decls;
  }

  @Override
  public void accept(Visitor v) throws VisitorException {
    v.visit(this);
  }

  @Override
  public void acceptReverse(Visitor v) throws VisitorException {
    v.visit(this);
  }
}
