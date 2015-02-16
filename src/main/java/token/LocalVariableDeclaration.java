package token;

import java.util.ArrayList;
import visitor.Visitor;
import exception.VisitorException;

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
