package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class Modifiers extends Token {

  private ArrayList<Modifier> modifiers;

  public boolean containsModifier(String modifier) {
    for (Modifier m : modifiers) {
      if (m.getLexeme().equals(modifier)) return true;
    }
    return false;
  }

  /**
   * Gets the list of modifiers
   */
  public ArrayList<Modifier> getModifiers() {
    return modifiers;
  }

  public Modifiers(ArrayList<Token> children) {
    super("", TokenType.Modifiers, children);
    modifiers = new ArrayList<Modifier>();
    if (children.get(0) instanceof Modifier) {
      lexeme = children.get(0).getLexeme();
      modifiers.add((Modifier) children.get(0));
    } else {
      Modifiers childModifiers = (Modifiers) children.get(0);
      modifiers.addAll(childModifiers.modifiers);
      modifiers.add((Modifier) children.get(1));
    }
  }

  @Override
  public void accept(Visitor v) throws VisitorException {
    v.visit(this);
  }

  @Override
  public void acceptReverse(Visitor v) throws VisitorException {
    v.visit(this);
  }

  public boolean isStatic() {
    for (Modifier modifier : modifiers) {
      if (modifier.getModifier().getTokenType() == TokenType.STATIC) return true;
    }
    return false;
  }

  public boolean isProtected() {
    for (Modifier modifier : modifiers) {
      if (modifier.getModifier().getTokenType() == TokenType.PROTECTED) return true;
    }
    return false;
  }
}
