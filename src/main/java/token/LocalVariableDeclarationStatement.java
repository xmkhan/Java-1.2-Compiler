package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class LocalVariableDeclarationStatement extends Token {

  public LocalVariableDeclarationStatement(ArrayList<Token> children) {
    super("", TokenType.LocalVariableDeclarationStatement, children);
  }

  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
