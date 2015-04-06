package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public abstract class BaseWhileStatement extends BaseStatement {

  public Expression expression;
  public Statement statement;
  public StatementNoShortIf statementNoShortIf;

  public Token newScope;
  public Token closeScope;

  public BaseWhileStatement(String lexeme, TokenType tokenType, ArrayList<Token> children) {
    super(lexeme, tokenType, children);
    for (Token token : children) {
      assignType(token);
    }

    // To handle implicit scopes, we explicitly add the scope.
    children.add(0, new Token("{", TokenType.LEFT_BRACE));
    children.add(new Token("}", TokenType.RIGHT_BRACE));
    newScope = children.get(0);
    closeScope = children.get(children.size() - 1);
  }

  private void assignType(Token token) {
    if (token instanceof Expression) {
      expression = (Expression) token;
    } else if (token instanceof Statement) {
      statement = (Statement) token;
    } else if(token instanceof StatementNoShortIf) {
      statementNoShortIf = (StatementNoShortIf) token;
    }
  }
}
