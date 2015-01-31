package token;

import java.util.ArrayList;
import visitor.Visitor;
import visitor.VisitorException;

public class CharLiteral extends Token {

  public CharLiteral(ArrayList<Token> children) {
    super(children.get(0).getLexeme(), TokenType.CHAR_LITERAL, children);
  }

  public void accept(Visitor v) throws VisitorException {
    v.visit(this);
  }
}
