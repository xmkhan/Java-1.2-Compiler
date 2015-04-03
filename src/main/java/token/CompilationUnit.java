package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class CompilationUnit extends Token {

  public PackageDeclaration packageDeclaration;
  public ImportDeclarations importDeclarations;
  public TypeDeclaration typeDeclaration;


  public CompilationUnit(ArrayList<Token> children) {
    super("", TokenType.CompilationUnit, children);
    for (Token child : children) {
      assignType(child);
    }
  }

  private void assignType(Token token) {
    if (token instanceof PackageDeclaration) {
      packageDeclaration = (PackageDeclaration) token;
    } else if (token instanceof ImportDeclarations) {
      importDeclarations = (ImportDeclarations) token;
    } else if (token instanceof TypeDeclaration) {
      typeDeclaration = (TypeDeclaration) token;
    }
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

  @Override
  public void traverse(Visitor v) throws VisitorException {
    v.visit(this);
  }
}
