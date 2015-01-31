package token;

import java.util.ArrayList;
import visitor.Visitor;
import visitor.VisitorException;

public class Modifier extends Token {

  private Token modifier;

  public Token getModifier() {
    return modifier;
  }

  public Modifier(ArrayList<Token> children) {
    super(children.get(0).getLexeme(), TokenType.Modifier, children);
    modifier = children.get(0);
  }

  public void accept(Visitor v) throws VisitorException {
    v.visit(this);
  }

  @Override
  public int hashCode() {
    return modifier.getTokenType().hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if(obj == null || !(obj instanceof  Modifier)) {
      return false;
    }

    return modifier.getTokenType().equals(((Modifier) obj).getTokenType());
  }
}
