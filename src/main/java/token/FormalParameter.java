package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class FormalParameter extends Declaration {
  public Type type;

  public FormalParameter(ArrayList<Token> children) {
    super("", TokenType.FormalParameter, children);
    type = (Type) children.get(0);
    identifier = children.get(1);
  }

  public void accept(Visitor v) throws VisitorException {
    v.visit(this);
  }
}
