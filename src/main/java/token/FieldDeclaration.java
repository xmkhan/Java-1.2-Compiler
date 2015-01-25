package token;

import java.util.ArrayList;
import visitor.Visitee;
import visitor.Visitor;

public class FieldDeclaration extends Token implements Visitee {

  public ArrayList<Token> children;

  public FieldDeclaration(ArrayList<Token> children) {
    super("", TokenType.FieldDeclaration);
  }

  public void accept(Visitor v) {
    v.visit(this);
  }
}
