package token;

import java.util.ArrayList;
import visitor.Visitor;
import exception.VisitorException;

public class BooleanLiteral extends Token {

  public BooleanLiteral(ArrayList<Token> children) {
    super(children.get(0).getLexeme(), TokenType.BooleanLiteral, children);
  }

  public void accept(Visitor v) throws VisitorException {
    v.visit(this);
  }
}
