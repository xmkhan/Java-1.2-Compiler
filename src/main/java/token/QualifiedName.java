package token;

import java.util.ArrayList;
import visitor.Visitor;
import visitor.VisitorException;

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
