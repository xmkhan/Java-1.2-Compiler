package token;

import java.util.ArrayList;
import visitor.Visitor;
import visitor.VisitorException;

public class IntLiteral extends Token {

  public ArrayList<Token> children;

  public IntLiteral(ArrayList<Token> children) {
    super(children.get(0).getLexeme(), TokenType.INT_LITERAL);
    this.children = children;
  }

  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
