package token;

import java.util.ArrayList;
import visitor.Visitor;
import visitor.VisitorException;

public class ReturnStatement extends Token {

  public ArrayList<Token> children;

  public ReturnStatement(ArrayList<Token> children) {
    super("", TokenType.ReturnStatement);
    this.children = children;
  }

  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
