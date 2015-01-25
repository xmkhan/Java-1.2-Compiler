package token;

import java.util.ArrayList;
import visitor.Visitee;
import visitor.Visitor;

public class Block extends Token implements Visitee {

  public ArrayList<Token> children;

  public Block(ArrayList<Token> children) {
    super("", TokenType.Block);
  }

  public void accept(Visitor v) {
    v.visit(this);
  }
}
