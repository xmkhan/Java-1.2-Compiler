package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class QualifiedName extends Token {
  private QualifiedName qualifiedName;
  private SimpleName simpleName;

  public QualifiedName(ArrayList<Token> children) {
    super("", TokenType.QualifiedName, children);
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
    if (token instanceof QualifiedName) {
      qualifiedName = (QualifiedName) token;
    } else if (token instanceof SimpleName) {
      simpleName = (SimpleName) token;
    }
  }
}
