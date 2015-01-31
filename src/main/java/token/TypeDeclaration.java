package token;

import java.util.ArrayList;
import visitor.Visitor;
import visitor.VisitorException;

public class TypeDeclaration extends Token {

  public TypeDeclaration(ArrayList<Token> children) {
    super("", TokenType.TypeDeclaration, children);
  }

  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
