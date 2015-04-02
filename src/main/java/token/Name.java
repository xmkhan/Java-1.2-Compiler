package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;
import java.util.List;

public class Name extends Token {
  public SimpleName simpleName;
  public QualifiedName qualifiedName;

  /**
   * The declaration path contains the list of declarations for 0..n-1 of the identifiers in a Name.
   * For example: a.b.c.d, declarationPath would contain the identifiers for a,b,c.
   */
  private List<Declaration> declarationPath;

  private List<Declaration> declarationTypes;
  private String absolutePath;

  private boolean usedInCast;

  public enum ClassifiedType { Ambiguous, NonStaticExpr, StaticExpr, Type, Package}

  public ClassifiedType classifiedType = ClassifiedType.Ambiguous;

  public String getAbsolutePath() {
    return absolutePath;
  }

  public void setAbsolutePath(String absolutePath) {
    this.absolutePath = absolutePath;
  }

  public List<Declaration> getDeclarationTypes() {
    return declarationTypes;
  }

  public void setDeclarationTypes(List<Declaration> declarationTypes) {
    this.declarationTypes = declarationTypes;
  }

  public List<Declaration> getDeclarationPath() {
    return declarationPath;
  }

  public void setDeclarationPath(List<Declaration> declarationPath) {
    this.declarationPath = declarationPath;
  }

  public void addDeclarationNode(Declaration declaration) {
    if (declarationPath == null) declarationPath = new ArrayList<Declaration>();
    declarationPath.add(declaration);
  }

  public void setUsedInCast() {
    usedInCast = true;
  }

  public boolean isUsedInCast() {
    return usedInCast;
  }

  public Name(ArrayList<Token> children) {
    super(children.get(0).getLexeme(), TokenType.Name, children);
    usedInCast = false;

    for (Token child : children) {
      if (child instanceof SimpleName) {
        this.simpleName = (SimpleName) child;
      } else if (child instanceof QualifiedName) {
        this.qualifiedName = (QualifiedName) child;
      }
    }
  }

  @Override
  public void accept(Visitor v) throws VisitorException {
    v.visit(this);
  }

  @Override
  public void acceptReverse(Visitor v) throws VisitorException {
    v.visit(this);
  }

  public boolean isSimple() {
    return !isQualified();
  }

  public boolean isQualified() {
    return getLexeme().contains(".");
  }
  public Token getName() {
    return simpleName != null ? simpleName : qualifiedName;
  }
}
