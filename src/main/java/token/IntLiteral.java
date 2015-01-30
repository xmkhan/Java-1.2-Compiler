package token;

import java.util.ArrayList;
import visitor.Visitor;
import visitor.VisitorException;

public class IntLiteral extends Token {

  public ArrayList<Token> children;
  public Integer value;

  public IntLiteral(ArrayList<Token> children) {
    super(children.get(0).getLexeme(), TokenType.INT_LITERAL);
    value = Integer.valueOf(children.get(0).getLexeme());
  }

  public void accept(Visitor v) throws VisitorException {
    v.visit(this);
  }
}
