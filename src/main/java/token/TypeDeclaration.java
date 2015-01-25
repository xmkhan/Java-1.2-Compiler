package token;

import java.util.ArrayList;
import visitor.Visitee;
import visitor.Visitor;

public class TypeDeclaration extends Token implements Visitee {

  public ArrayList<Token> children;

  public TypeDeclaration(ArrayList<Token> children) {
    super("", TokenType.TypeDeclaration);
  }

  public void accept(Visitor v) {
    v.visit(this);
  }
}
