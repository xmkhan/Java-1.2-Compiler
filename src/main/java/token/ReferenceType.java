package token;

import java.util.ArrayList;
import visitor.Visitor;
import visitor.VisitorException;

public class ReferenceType extends Token {

  public ArrayList<Token> children;

  public ReferenceType(ArrayList<Token> children) {
    super("", TokenType.ReferenceType);
    this.children = children;
  }

  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
