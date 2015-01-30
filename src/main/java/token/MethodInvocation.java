package token;

import java.util.ArrayList;
import visitor.Visitor;
import visitor.VisitorException;

public class MethodInvocation extends Token {

  public ArrayList<Token> children;

  public MethodInvocation(ArrayList<Token> children) {
    super("", TokenType.MethodInvocation);
    this.children = children;
  }

  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
