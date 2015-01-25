package token;

import java.util.ArrayList;
import visitor.Visitee;
import visitor.Visitor;

public class InterfaceDeclaration extends Token implements Visitee {

  public ArrayList<Token> children;

  public InterfaceDeclaration(ArrayList<Token> children) {
    super("", TokenType.InterfaceDeclaration);
  }

  public void accept(Visitor v) {
    v.visit(this);
  }
}
