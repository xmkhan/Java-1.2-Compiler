package token;

import java.util.ArrayList;
import visitor.Visitor;
import visitor.VisitorException;

public class ArgumentList extends Token {

  public ArrayList<Token> children;

  public ArgumentList(ArrayList<Token> children) {
    super("", TokenType.ArgumentList);
    this.children = children;
  }

  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
