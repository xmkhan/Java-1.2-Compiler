package token;

import java.util.ArrayList;
import visitor.Visitee;
import visitor.Visitor;

public class TypeDeclarations extends Token implements Visitee {

  public ArrayList<Token> children;

  public TypeDeclarations(ArrayList<Token> children) {
    super("", TokenType.TypeDeclarations);
  }

  public void accept(Visitor v) {
    v.visit(this);
  }
}
