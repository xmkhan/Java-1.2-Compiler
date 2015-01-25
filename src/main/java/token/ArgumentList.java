package token;

import java.util.ArrayList;
import visitor.Visitee;
import visitor.Visitor;

public class ArgumentList extends Token implements Visitee {

  public ArrayList<Token> children;

  public ArgumentList(ArrayList<Token> children) {
    super("", TokenType.ArgumentList);
  }

  public void accept(Visitor v) {
    v.visit(this);
  }
}
