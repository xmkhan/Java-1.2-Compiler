package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class LocalVariableDeclaration extends Token {

  public LocalVariableDeclaration(ArrayList<Token> children) {
    super("", TokenType.LocalVariableDeclaration, children);
  }

  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
