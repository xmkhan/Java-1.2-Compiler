package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class ConstructorDeclarator extends Token {
  private SimpleName simpleName;
  private FormalParameterList formalParameterList;

  public FormalParameterList paramList;

  public ConstructorDeclarator(ArrayList<Token> children) {
    super(children.get(0).getLexeme(), TokenType.ConstructorDeclarator, children);
    for (Token token : children) {
      assignType(token);
    }
  }

  @Override
  public void accept(Visitor v) throws VisitorException {
    if (paramList != null) paramList.accept(v);
    v.visit(this);
  }

  @Override
  public void acceptReverse(Visitor v) throws VisitorException {
    v.visit(this);
    if (paramList != null) paramList.acceptReverse(v);
  }

  public FormalParameterList getParameterList() {
    return formalParameterList != null ? formalParameterList : null;
  }

  public Token getIdentifier() {
    return simpleName.getIdentifier();
  }

  private void assignType(Token token) {
    if (token instanceof FormalParameterList) {
      formalParameterList = (FormalParameterList) token;
    } else if (token instanceof SimpleName) {
      simpleName = (SimpleName) token;
    }
  }
}
