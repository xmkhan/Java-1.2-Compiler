package token;

import java.util.ArrayList;
import visitor.Visitee;
import visitor.Visitor;

public class BlockStatements extends Token implements Visitee {

  public ArrayList<Token> children;

  public BlockStatements(ArrayList<Token> children) {
    super("", TokenType.BlockStatements);
  }

  public void accept(Visitor v) {
    v.visit(this);
  }
}
