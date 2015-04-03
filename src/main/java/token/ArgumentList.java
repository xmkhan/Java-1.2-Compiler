package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class ArgumentList extends Token {

  ArrayList<Expression> argumentList;

  public ArgumentList(ArrayList<Token> children) {
    super("", TokenType.ArgumentList, children);

    argumentList = new ArrayList<Expression>();
    if (children.get(0) instanceof Expression) {
      lexeme = children.get(0).getLexeme();
      argumentList.add((Expression) children.get(0));
    } else {
      ArgumentList childArguments = (ArgumentList) children.get(0);
      argumentList.addAll(childArguments.argumentList);
      argumentList.add((Expression) children.get(2));
    }
  }

  public ArrayList<Expression> getArgumentList() {
    return argumentList;
  }

  public int numArguments() {
    return argumentList.size();
  }

  public void accept(Visitor v) throws VisitorException {
    for (Expression expression : argumentList) {
      expression.accept(v);
    }
    v.visit(this);
  }

  @Override
  public void acceptReverse(Visitor v) throws VisitorException {
    v.visit(this);
    for (Expression expression : argumentList) {
      expression.acceptReverse(v);
    }
  }

  @Override
  public void traverse(Visitor v) throws VisitorException {
    v.visit(this);
  }
}
