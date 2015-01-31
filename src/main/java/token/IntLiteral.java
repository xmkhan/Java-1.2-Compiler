package token;

import java.util.ArrayList;
import visitor.Visitor;
import visitor.VisitorException;

public class IntLiteral extends Token {

  public IntLiteral(ArrayList<Token> children) {
    super(children.get(0).getLexeme(), TokenType.INT_LITERAL, children);
  }

  public void accept(Visitor v) throws VisitorException {
    v.visit(this);
  }
}
