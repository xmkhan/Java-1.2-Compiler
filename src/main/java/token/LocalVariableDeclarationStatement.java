package token;

import java.util.ArrayList;
import visitor.Visitee;
import visitor.Visitor;

public class LocalVariableDeclarationStatement extends Token implements Visitee {

  public ArrayList<Token> children;

  public LocalVariableDeclarationStatement(ArrayList<Token> children) {
    super("", TokenType.LocalVariableDeclarationStatement);
  }

  public void accept(Visitor v) {
    v.visit(this);
  }
}
