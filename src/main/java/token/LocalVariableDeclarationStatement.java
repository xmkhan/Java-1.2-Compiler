package token;

import java.util.ArrayList;
import visitor.Visitor;

public class LocalVariableDeclarationStatement extends Token {

  public ArrayList<Token> children;

  public LocalVariableDeclarationStatement(ArrayList<Token> children) {
    super("", TokenType.LocalVariableDeclarationStatement);
    this.children = children;
  }

  public void accept(Visitor v) {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}