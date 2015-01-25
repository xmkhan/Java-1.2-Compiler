package token;

import java.util.ArrayList;
import visitor.Visitee;
import visitor.Visitor;

public class BlockStatement extends Token implements Visitee {

  public ArrayList<Token> children;

  public BlockStatement(ArrayList<Token> children) {
    super("", TokenType.BlockStatement);
  }

  public void accept(Visitor v) {
    v.visit(this);
  }
}
