package token;

import java.util.ArrayList;
import visitor.Visitor;
import visitor.VisitorException;

public class ConditionalOrExpression extends Token {

  public ArrayList<Token> children;

  public ConditionalOrExpression(ArrayList<Token> children) {
    super("", TokenType.ConditionalOrExpression);
    this.children = children;
  }

  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
