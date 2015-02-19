package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class Name extends Token {
  private SimpleName simpleName;
  private QualifiedName qualifiedName;

  public Name(ArrayList<Token> children) {
    super("", TokenType.Name, children);
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

  private void assignType(Token token) {
    if (token instanceof SimpleName) {
      simpleName = (SimpleName) token;
    } else if (token instanceof QualifiedName) {
      qualifiedName = (QualifiedName) token;
    }
  }

  public Token getSimpleName() {
    return simpleName != null ? simpleName.getIdentifier() : null;
  }
}
