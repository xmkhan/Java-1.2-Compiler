package token;

import java.util.ArrayList;
import visitor.Visitor;
import visitor.VisitorException;

public class CharLiteral extends Token {

  public ArrayList<Token> children;

  public CharLiteral(ArrayList<Token> children) {
    super(children.get(0).getLexeme(), TokenType.CHAR_LITERAL);
    this.children = children;
  }

  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
