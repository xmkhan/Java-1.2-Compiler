package token;

import java.util.ArrayList;
import visitor.Visitor;
import visitor.VisitorException;

public class CharLiteral extends Token {

  public Character value;

  public CharLiteral(ArrayList<Token> children) {
    super(children.get(0).getLexeme(), TokenType.CHAR_LITERAL);
    value = lexeme.charAt(0);
  }

  public void accept(Visitor v) throws VisitorException {
    v.visit(this);
  }
}
