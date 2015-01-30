package token;

import java.util.ArrayList;
import visitor.Visitor;
import visitor.VisitorException;

public class QualifiedName extends Token {

  public ArrayList<Token> children;

  public QualifiedName(ArrayList<Token> children) {
    super("", TokenType.QualifiedName);
    this.children = children;
  }

  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
