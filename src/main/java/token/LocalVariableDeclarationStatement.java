package token;

import java.util.ArrayList;
import visitor.Visitor;
import visitor.VisitorException;

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
