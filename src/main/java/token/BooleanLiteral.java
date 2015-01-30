package token;

import java.util.ArrayList;
import visitor.Visitor;
import visitor.VisitorException;

public class BooleanLiteral extends Token {

  public ArrayList<Token> children;

  public BooleanLiteral(ArrayList<Token> children) {
    super(children.get(0).getLexeme(), TokenType.BooleanLiteral);
    this.children = children;
  }

  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
