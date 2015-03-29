package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class ForStatementNoShortIf extends BaseStatement {

  public ForInit forInit;
  public ForUpdate forUpdate;
  public StatementNoShortIf statement;
  public Expression expression;

  public ForStatementNoShortIf(ArrayList<Token> children) {
    super("", TokenType.ForStatementNoShortIf, children);
    for (Token token : children) {
      assignType(token);
    }

    // To handle implicit scopes, we explicitly add the scope.
    children.add(0, new Token("{", TokenType.LEFT_BRACE));
    children.add(new Token("}", TokenType.RIGHT_BRACE));
  }

  private void assignType(Token token) {
    if (token instanceof ForInit) {
      forInit = (ForInit) token;
    } else if (token instanceof ForUpdate) {
      forUpdate = (ForUpdate) token;
    } else if (token instanceof Statement) {
      statement = (StatementNoShortIf) token;
    } else if(token instanceof Expression) {
      expression = (Expression) token;
    }
  }

  @Override
  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }

  @Override
  public void acceptReverse(Visitor v) throws VisitorException {
    v.visit(this);
    for (Token token : children) {
      token.acceptReverse(v);
    }
  }

  @Override
  public void traverse(Visitor v) throws VisitorException {
    v.visit(this);
  }
}
