package token;

import java.util.ArrayList;
import visitor.Visitor;
import visitor.VisitorException;

public class StringLiteral extends Token {

  public ArrayList<Token> children;

  public StringLiteral(ArrayList<Token> children) {
    super("", TokenType.STR_LITERAL);
    this.children = children;
  }

  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
