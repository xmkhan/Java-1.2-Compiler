package token;

import java.util.ArrayList;
import visitor.Visitor;
import visitor.VisitorException;

public class WhileStatement extends Token {

  public ArrayList<Token> children;

  public WhileStatement(ArrayList<Token> children) {
    super("", TokenType.WhileStatement);
    this.children = children;
  }

  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
