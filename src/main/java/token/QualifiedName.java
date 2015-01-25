package token;

import java.util.ArrayList;
import visitor.Visitor;

public class QualifiedName extends Token {

  public ArrayList<Token> children;

  public QualifiedName(ArrayList<Token> children) {
    super("", TokenType.QualifiedName);
    this.children = children;
  }

  public void accept(Visitor v) {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
