package token;

import java.util.ArrayList;
import visitor.Visitor;
import visitor.VisitorException;

public class IfThenStatement extends Token {

  public ArrayList<Token> children;

  public IfThenStatement(ArrayList<Token> children) {
    super("", TokenType.IfThenStatement);
    this.children = children;
  }

  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
