package token;

import java.util.ArrayList;
import visitor.Visitor;
import visitor.VisitorException;

public class ForUpdate extends Token {

  public ArrayList<Token> children;

  public ForUpdate(ArrayList<Token> children) {
    super("", TokenType.ForUpdate);
    this.children = children;
  }

  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
