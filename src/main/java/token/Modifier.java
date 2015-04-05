package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class Modifier extends Token {

  private Token modifier;

  public Token getModifier() {
    return modifier;
  }

  public Modifier(ArrayList<Token> children) {
    super(children.get(0).getLexeme(), TokenType.Modifier, children);
    modifier = children.get(0);
  }

  @Override
  public void accept(Visitor v) throws VisitorException {
    v.visit(this);
  }

  @Override
  public void acceptReverse(Visitor v) throws VisitorException {
    v.visit(this);
  }

  @Override
  public void traverse(Visitor v) throws VisitorException {
    v.visit(this);
  }

  @Override
  public int hashCode() {
    return modifier.getTokenType().hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null || !(obj instanceof Modifier)) {
      return false;
    }

    return modifier.getTokenType().equals(((Modifier) obj).getTokenType());
  }
}
