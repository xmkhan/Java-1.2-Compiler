package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class ForStatement extends BaseForStatement {
  public Statement statement;


  public ForStatement(ArrayList<Token> children) {
    super("", TokenType.ForStatement, children);
    for (Token token : children) {
      assignType(token);
    }
  }

  private void assignType(Token token) {
    if (token instanceof Statement) {
      statement = (Statement) token;
    }
  }

  @Override
  public Token getStatement() {
    return statement;
  }
}
