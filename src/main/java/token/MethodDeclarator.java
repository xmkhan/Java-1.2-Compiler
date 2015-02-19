package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class MethodDeclarator extends Token {
  public FormalParameterList formalParameterList;
  public String identifier;

  public MethodDeclarator(ArrayList<Token> children) {
    super("", TokenType.MethodDeclarator, children);
    for (Token token : children) {
      assignType(token);
    }
  }

  private void assignType(Token token) {
    if (token instanceof FormalParameterList) {
      formalParameterList = (FormalParameterList) token;
    } else if (token.getTokenType().equals(TokenType.IDENTIFIER)) {
      identifier = token.getLexeme();
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
}
