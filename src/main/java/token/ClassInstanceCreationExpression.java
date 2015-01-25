package token;

import java.util.ArrayList;
import visitor.Visitor;

public class ClassInstanceCreationExpression extends Token {

  public ArrayList<Token> children;

  public ClassInstanceCreationExpression(ArrayList<Token> children) {
    super("", TokenType.ClassInstanceCreationExpression);
    this.children = children;
  }

  public void accept(Visitor v) {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
