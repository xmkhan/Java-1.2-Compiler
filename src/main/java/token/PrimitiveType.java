package token;

import java.util.ArrayList;
import visitor.Visitor;
import visitor.VisitorException;

public class PrimitiveType extends Token {

  public ArrayList<Token> children;

  public PrimitiveType(ArrayList<Token> children) {
    super("", TokenType.PrimitiveType);
    this.children = children;
  }

  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
