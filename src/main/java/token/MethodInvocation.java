package token;

import java.util.ArrayList;
import visitor.Visitee;
import visitor.Visitor;

public class MethodInvocation extends Token implements Visitee {

  public ArrayList<Token> children;

  public MethodInvocation(ArrayList<Token> children) {
    super("", TokenType.MethodInvocation);
  }

  public void accept(Visitor v) {
    v.visit(this);
  }
}
