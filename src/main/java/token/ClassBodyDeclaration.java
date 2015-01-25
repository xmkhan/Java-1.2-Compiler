package token;

import java.util.ArrayList;
import visitor.Visitee;
import visitor.Visitor;

public class ClassBodyDeclaration extends Token implements Visitee {

  public ArrayList<Token> children;

  public ClassBodyDeclaration(ArrayList<Token> children) {
    super("", TokenType.ClassBodyDeclaration);
  }

  public void accept(Visitor v) {
    v.visit(this);
  }
}
