package token;

import java.util.ArrayList;
import visitor.Visitor;
import visitor.VisitorException;

public class StringLiteral extends Token {

  public ArrayList<Token> children;
  public String value;

  public StringLiteral(ArrayList<Token> children) {
    super(children.get(0).getLexeme(), TokenType.STR_LITERAL);
    value = lexeme;
  }

  public void accept(Visitor v) throws VisitorException {
    v.visit(this);
  }
}
