package token;

import java.util.ArrayList;
import visitor.Visitor;
import visitor.VisitorException;

public class ArgumentList extends Token {

  public ArgumentList(ArrayList<Token> children) {
    super("", TokenType.ArgumentList, children);
  }

  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
