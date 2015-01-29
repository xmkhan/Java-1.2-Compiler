package token;

import java.util.ArrayList;
import visitor.Visitor;
import visitor.VisitorException;

public class EmptyStatement extends Token {

  public ArrayList<Token> children;

  public EmptyStatement(ArrayList<Token> children) {
    super("", TokenType.EmptyStatement);
    this.children = children;
  }

  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
