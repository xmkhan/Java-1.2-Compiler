package token;

import java.util.ArrayList;
import visitor.Visitor;
import exception.VisitorException;

public class ExtendsInterfaces extends Token {

  public ExtendsInterfaces(ArrayList<Token> children) {
    super("", TokenType.ExtendsInterfaces, children);
  }

  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
