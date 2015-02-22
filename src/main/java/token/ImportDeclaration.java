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

  public boolean containsSuffix(String suffix) {
    return getSuffix().equals(suffix);
  }

  public String getSuffix() {
    String[] names = getLexeme().split("\\.");
    return names[names.length - 1];
  }

  public boolean isSingle() {
    return !onDemand;
  }

  public boolean isOnDemand() {
    return onDemand;
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
