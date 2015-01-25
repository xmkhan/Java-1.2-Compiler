package token;

import java.util.ArrayList;
import visitor.Visitee;
import visitor.Visitor;

public class ClassInstanceCreationExpression extends Token implements Visitee {

  public ArrayList<Token> children;

  public ClassInstanceCreationExpression(ArrayList<Token> children) {
    super("", TokenType.ClassInstanceCreationExpression);
  }

  public void accept(Visitor v) {
    v.visit(this);
  }
}
