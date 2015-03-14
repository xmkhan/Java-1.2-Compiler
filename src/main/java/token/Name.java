package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;
import java.util.List;

public class Name extends Token {
  public SimpleName simpleName;
  public QualifiedName qualifiedName;

  private List<Declaration> declarationTypes;
  private String absolutePath;

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

  public Name(ArrayList<Token> children) {
    super(children.get(0).getLexeme(), TokenType.Name, children);
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
