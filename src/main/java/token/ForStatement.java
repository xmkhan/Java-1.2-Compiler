package token;

import java.util.ArrayList;
import visitor.Visitor;
import visitor.VisitorException;

public class ForStatement extends Token {

  public ArrayList<Token> children;

  public ForStatement(ArrayList<Token> children) {
    super("", TokenType.ForStatement);
    this.children = children;
  }

  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
