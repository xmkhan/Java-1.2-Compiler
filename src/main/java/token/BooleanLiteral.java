package token;

import java.util.ArrayList;
import visitor.Visitor;
import visitor.VisitorException;

public class BooleanLiteral extends Token {

  public ArrayList<Token> children;

  public Boolean value;

  public BooleanLiteral(ArrayList<Token> children) {
    super(children.get(0).getLexeme(), TokenType.BooleanLiteral);
    value = Boolean.valueOf(lexeme);
  }

  public void accept(Visitor v) throws VisitorException {
    v.visit(this);
  }
}
