package token;

import java.util.ArrayList;

public class IfThenElseStatement extends BaseIfThenElse {
  public Statement elseStatement;

  public IfThenElseStatement(ArrayList<Token> children) {
    super("", TokenType.IfThenElseStatement, children);
    elseStatement = (Statement) children.get(6);
  }

  @Override
  public BaseStatement getElseStatement() {
    return elseStatement;
  }
}
