package token;

import java.util.ArrayList;
import visitor.Visitor;

public class ClassMemberDeclaration extends Token {

  public ArrayList<Token> children;

  public ClassMemberDeclaration(ArrayList<Token> children) {
    super("", TokenType.ClassMemberDeclaration);
    this.children = children;
  }

  public void accept(Visitor v) {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
