package token;

import java.util.ArrayList;
import java.util.List;

import visitor.Visitor;
import visitor.VisitorException;

public class CastExpression extends Token {
  private Name name = null;
  private boolean isExpression;

  public ArrayList<Token> children;

  public CastExpression(ArrayList<Token> children) {
    super("", TokenType.CastExpression);
    isExpression = false;
    this.children = children;
    checkExpression();
  }

  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }

  private void checkExpression() {
    Token token = children.get(1);
    int child = 0;

    if (!(token instanceof Expression)) return;

    isExpression = true;

    while (true) {
      switch (child) {
        case 0:
          if (token instanceof Expression) token = ((Expression) token).children.get(0);
          else return;
          break;
        case 1:
          if (token instanceof AssignmentExpression) token = ((AssignmentExpression) token).children.get(0);
          else return;
          break;
        case 2:
          if (token instanceof ConditionalOrExpression) token = ((ConditionalOrExpression) token).children.get(0);
          else return;
          break;
        case 3:
          if (token instanceof ConditionalAndExpression) token = ((ConditionalAndExpression) token).children.get(0);
          else return;
          break;
        case 4:
          if (token instanceof InclusiveOrExpression) token = ((InclusiveOrExpression) token).children.get(0);
          else return;
          break;
        case 5:
          if (token instanceof AndExpression) token = ((AndExpression) token).children.get(0);
          else return;
          break;
        case 6:
          if (token instanceof EqualityExpression) token = ((EqualityExpression) token).children.get(0);
          else return;
          break;
        case 7:
          if (token instanceof RelationalExpression) token = ((RelationalExpression) token).children.get(0);
          else return;
          break;
        case 8:
          if (token instanceof AdditiveExpression) token = ((AdditiveExpression) token).children.get(0);
          else return;
          break;
        case 9:
          if (token instanceof MultiplicativeExpression) token = ((MultiplicativeExpression) token).children.get(0);
          else return;
          break;
        case 10:
          if (token instanceof UnaryExpression) token = ((UnaryExpression) token).children.get(0);
          else return;
          break;
        case 11:
          if (token instanceof UnaryExpressionNotMinus) token = ((UnaryExpressionNotMinus) token).children.get(0);
          else return;
          break;
        case 12:
          if (token instanceof Name) {
            name = (Name) token;
          }
          return;
      }
      child++;
    }
  }

  public boolean isExpression() {
    return isExpression;
  }

  public boolean isName() {
    return name != null;
  }
}
