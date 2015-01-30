package token;

import java.util.ArrayList;
import visitor.Visitor;
import visitor.VisitorException;

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

  public void accept(Visitor v) throws VisitorException {
    v.visit(this);
  }
}
