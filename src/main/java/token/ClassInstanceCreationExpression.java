package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class ClassInstanceCreationExpression extends Token {

  public ClassInstanceCreationExpression(ArrayList<Token> children) {
    super("", TokenType.ClassInstanceCreationExpression, children);
  }

  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
