package token;

import java.util.ArrayList;
import visitor.Visitee;
import visitor.Visitor;

public class ClassDeclaration extends Token implements Visitee {

  public ArrayList<Token> children;

  public ClassDeclaration(ArrayList<Token> children) {
    super("", TokenType.ClassDeclaration);
  }

  public void accept(Visitor v) {
    v.visit(this);
  }
}
