package token;

import java.util.ArrayList;
import visitor.Visitee;
import visitor.Visitor;

public class ClassMemberDeclaration extends Token implements Visitee {

  public ArrayList<Token> children;

  public ClassMemberDeclaration(ArrayList<Token> children) {
    super("", TokenType.ClassMemberDeclaration);
  }

  public void accept(Visitor v) {
    v.visit(this);
  }
}
