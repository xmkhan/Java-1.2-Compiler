package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

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
