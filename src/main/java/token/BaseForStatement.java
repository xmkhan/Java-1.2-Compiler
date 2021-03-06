package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;


public abstract class BaseForStatement extends BaseStatement {
  public ForInit forInit;
  public ForUpdate forUpdate;
  public Expression expression;

  public Token newScope;
  public Token closeScope;

  public BaseForStatement(String lexeme, TokenType tokenType, ArrayList<Token> children) {
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
    if (token instanceof ForInit) {
      forInit = (ForInit) token;
    } else if (token instanceof ForUpdate) {
      forUpdate = (ForUpdate) token;
    } else if(token instanceof Expression) {
      expression = (Expression) token;
    }
  }

  public abstract Token getStatement();
}
