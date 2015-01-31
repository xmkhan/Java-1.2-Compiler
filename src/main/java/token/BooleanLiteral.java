package token;

import java.util.ArrayList;
import visitor.Visitor;
import visitor.VisitorException;

public class BooleanLiteral extends Token {

  public BooleanLiteral(ArrayList<Token> children) {
    super(children.get(0).getLexeme(), TokenType.BooleanLiteral);
  }

  public void accept(Visitor v) throws VisitorException {
    v.visit(this);
  }
}
