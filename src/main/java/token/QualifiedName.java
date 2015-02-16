package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class QualifiedName extends Token {

  public QualifiedName(ArrayList<Token> children) {
    super("", TokenType.QualifiedName, children);
  }

  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
