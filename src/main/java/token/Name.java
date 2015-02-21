package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class Name extends Token {
  SimpleName simpleName;
  QualifiedName qualifiedName;

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
