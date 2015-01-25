package token;

import java.util.ArrayList;
import visitor.Visitee;
import visitor.Visitor;

public class MethodHeader extends Token implements Visitee {

  public ArrayList<Token> children;

  public MethodHeader(ArrayList<Token> children) {
    super("", TokenType.MethodHeader);
  }

  public void accept(Visitor v) {
    v.visit(this);
  }
}
