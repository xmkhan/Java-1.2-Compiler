package token;

import java.util.ArrayList;
import visitor.Visitor;
import visitor.VisitorException;

public class ClassMemberDeclaration extends Token {

  public ClassMemberDeclaration(ArrayList<Token> children) {
    super("", TokenType.ClassMemberDeclaration, children);
  }

  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
