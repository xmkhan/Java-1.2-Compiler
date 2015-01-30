package token;

import java.util.ArrayList;
import visitor.Visitor;
import visitor.VisitorException;

public class ForInit extends Token {

  public ArrayList<Token> children;

  public ForInit(ArrayList<Token> children) {
    super("", TokenType.ForInit);
    this.children = children;
  }

  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
