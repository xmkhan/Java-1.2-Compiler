package token;

import java.util.ArrayList;
import visitor.Visitee;
import visitor.Visitor;

public class LocalVariableDeclaration extends Token implements Visitee {

  public ArrayList<Token> children;

  public LocalVariableDeclaration(ArrayList<Token> children) {
    super("", TokenType.LocalVariableDeclaration);
  }

  public void accept(Visitor v) {
    v.visit(this);
  }
}
