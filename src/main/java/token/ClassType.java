package token;

import java.util.ArrayList;
import visitor.Visitor;
import visitor.VisitorException;

public class ClassType extends Token {

  public ArrayList<Token> children;

  public ClassType(ArrayList<Token> children) {
    super("", TokenType.ClassType);
    this.children = children;
  }

  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
