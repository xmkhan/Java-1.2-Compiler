package token;

import java.util.ArrayList;
import visitor.Visitor;

public class Modifiers extends Token {

  private ArrayList<Modifier> modifiers;

  /**
   * Gets the list of modifiers
   */
  public ArrayList<Modifier> getModifiers() {
    return modifiers;
  }

  public Modifiers(ArrayList<Token> children) {
    super("", TokenType.Modifiers);
    modifiers = new ArrayList<>();
    if (children.get(0) instanceof Modifier) {
      lexeme = children.get(0).getLexeme();
      modifiers.add((Modifier) children.get(0));
    } else {
      Modifiers childModifiers = (Modifiers) children.get(0);
     modifiers.addAll(childModifiers.modifiers);
    }
  }

  public void accept(Visitor v) {
    v.visit(this);
  }
}
