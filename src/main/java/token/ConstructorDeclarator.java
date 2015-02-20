package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class ConstructorDeclarator extends Token {
  private SimpleName simpleName;
  private FormalParameterList formalParameterList;

  public ConstructorDeclarator(ArrayList<Token> children) {
    super("", TokenType.ConstructorDeclarator, children);
    for (Token token : children) {
      assignType(token);
    }
  }

  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
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
