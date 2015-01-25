package token;

import java.util.ArrayList;
import visitor.Visitee;
import visitor.Visitor;

public class ClassBodyDeclarations extends Token implements Visitee {

  public ArrayList<Token> children;

  public ClassBodyDeclarations(ArrayList<Token> children) {
    super("", TokenType.ClassBodyDeclarations);
  }

  public void accept(Visitor v) {
    v.visit(this);
  }
}
