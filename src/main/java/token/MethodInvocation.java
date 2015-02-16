package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class MethodInvocation extends Token {

  public MethodInvocation(ArrayList<Token> children) {
    super("", TokenType.MethodInvocation, children);
  }

  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
