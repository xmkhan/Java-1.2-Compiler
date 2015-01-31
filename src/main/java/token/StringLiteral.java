package token;

import java.util.ArrayList;
import visitor.Visitor;
import visitor.VisitorException;

public class StringLiteral extends Token {

  public StringLiteral(ArrayList<Token> children) {
    super(children.get(0).getLexeme(), TokenType.STR_LITERAL);
  }

  public void accept(Visitor v) throws VisitorException {
    v.visit(this);
  }
}
