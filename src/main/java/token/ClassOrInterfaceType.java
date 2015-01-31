package token;

import java.util.ArrayList;
import visitor.Visitor;
import visitor.VisitorException;

public class ClassOrInterfaceType extends Token {

  public ClassOrInterfaceType(ArrayList<Token> children) {
    super("", TokenType.ClassOrInterfaceType, children);
  }

  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
