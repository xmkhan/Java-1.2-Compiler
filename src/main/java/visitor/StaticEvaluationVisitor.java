package visitor;

import exception.ReachabilityVisitorException;
import exception.StaticEvaluationVisitorException;
import exception.VisitorException;
import token.*;

import java.util.ArrayList;
import java.util.Stack;

public class StaticEvaluationVisitor extends BaseVisitor {

 private Stack<Literal> results;

 public StaticEvaluationVisitor() {
   results = new Stack<Literal>();
 }

  public boolean getLastValue() throws VisitorException {
    if(results.size() != 1) {
      throw new StaticEvaluationVisitorException("Too many or too little elements in stack", null);
    }

    return Boolean.valueOf(results.pop().getLexeme());
  }

  @Override
  public void visit(Literal token) throws VisitorException {
    super.visit(token);

    if(token.isStringLiteral()) {
      throw new StaticEvaluationVisitorException("Can not evaluate", token);
    }

    results.push(token);
  }

  public void visit(RelationalExpression token) throws VisitorException {
    super.visit(token);

    if(token.children.size() == 1) return;
    if(token.children.get(1).getLexeme().equals("instanceof")) {
      throw new StaticEvaluationVisitorException("instance of not supported", token);
    }

    Literal second = results.pop();
    Literal first = results.pop();

    int secondValue = Integer.parseInt(second.getLexeme());
    int firstValue = Integer.parseInt(first.getLexeme());

    if (token.children.get(1).getLexeme().equals("<")) {
      results.push(constructLiteralResult(firstValue < secondValue));
    } else if (token.children.get(1).getLexeme().equals(">")) {
      results.push(constructLiteralResult(firstValue > secondValue));
    } else if (token.children.get(1).getLexeme().equals("<=")) {
      results.push(constructLiteralResult(firstValue <= secondValue));
    } else if (token.children.get(1).getLexeme().equals(">=")) {
      results.push(constructLiteralResult(firstValue >= secondValue));
    }
  }

  public void visit(EqualityExpression token) throws VisitorException {
    super.visit(token);

    if(token.children.size() == 1) return;

    Literal second = results.pop();
    Literal first = results.pop();

    if(second.getLexeme().equals(first)) {
      results.push(constructLiteralResult(true));
    } else {
      results.push(constructLiteralResult(false));
    }
  }

  public void visit(ConditionalAndExpression token) throws VisitorException {
    super.visit(token);

    if(token.children.size() == 1) return;

    Literal second = results.pop();
    Literal first = results.pop();

    boolean secondValue = Boolean.parseBoolean(second.getLexeme());
    boolean firstValue = Boolean.parseBoolean(first.getLexeme());
    results.push(constructLiteralResult(secondValue && firstValue));
  }

  public void visit(AndExpression token) throws VisitorException {
    super.visit(token);

    if(token.children.size() == 1) return;

    Literal second = results.pop();
    Literal first = results.pop();

    boolean secondValue = Boolean.parseBoolean(second.getLexeme());
    boolean firstValue = Boolean.parseBoolean(first.getLexeme());
    results.push(constructLiteralResult(secondValue & firstValue));
  }

  public void visit(ConditionalOrExpression token) throws VisitorException {
    super.visit(token);

    if(token.children.size() == 1) return;

    Literal second = results.pop();
    Literal first = results.pop();

    boolean secondValue = Boolean.parseBoolean(second.getLexeme());
    boolean firstValue = Boolean.parseBoolean(first.getLexeme());
    results.push(constructLiteralResult(secondValue || firstValue));
  }

  public void visit(InclusiveOrExpression token) throws VisitorException {
    super.visit(token);

    if(token.children.size() == 1) return;

    Literal second = results.pop();
    Literal first = results.pop();

    boolean secondValue = Boolean.parseBoolean(second.getLexeme());
    boolean firstValue = Boolean.parseBoolean(first.getLexeme());
    results.push(constructLiteralResult(secondValue | firstValue));
  }

  public void visit(MethodInvocation token) throws VisitorException {
    super.visit(token);
    throw new StaticEvaluationVisitorException("Method invocation can not be analyzed", token);
  }

  public void visit(ClassInstanceCreationExpression token) throws VisitorException {
    super.visit(token);
    throw new StaticEvaluationVisitorException("ClassInstanceCreationExpression can not be analyzed", token);
  }

  public void visit(ArrayCreationExpression token) throws VisitorException {
    super.visit(token);
    throw new StaticEvaluationVisitorException("ArrayCreationExpression can not be analyzed", token);
  }

  public void visit(FieldAccess token) throws VisitorException {
    super.visit(token);
    throw new StaticEvaluationVisitorException("FieldAccess can not be analyzed", token);
  }

  public void visit(ArrayAccess token) throws VisitorException {
    super.visit(token);
    throw new StaticEvaluationVisitorException("ArrayAccess can not be analyzed", token);
  }

  public void visit(Name token) throws VisitorException {
    super.visit(token);
    throw new StaticEvaluationVisitorException("Name can not be analyzed", token);
  }

  public void visit(Primary token) throws VisitorException {
    super.visit(token);
    if(token.children.size() == 1 && token.children.get(0).equals("this")) {
      throw new StaticEvaluationVisitorException("this can not be analyzed", token);
    }
  }

  public void visit(MultiplicativeExpression token) throws VisitorException {
    if(token.children.size() == 1) return;

    Literal second = results.pop();
    Literal first = results.pop();

    int secondValue = Integer.parseInt(second.getLexeme());
    int firstValue = Integer.parseInt(first.getLexeme());

    int result;
    if(token.children.get(1).getLexeme().equals("*")) {
      result = firstValue * secondValue;
    } else if(token.children.get(1).getLexeme().equals("/")) {
      result = firstValue / secondValue;
    } else {
      result = firstValue % secondValue;
    }
    results.push(constructLiteralResult(result));
  }

  public void visit(AdditiveExpression token) throws VisitorException {
    if(token.children.size() == 1) return;

    Literal second = results.pop();
    Literal first = results.pop();

    int secondValue = Integer.parseInt(second.getLexeme());
    int firstValue = Integer.parseInt(first.getLexeme());

    int result;
    if(token.children.get(1).getLexeme().equals("+")) {
      result = firstValue + secondValue;
    } else
    {
      result = firstValue - secondValue;
    }
    results.push(constructLiteralResult(result));
  }

  private Literal constructLiteralResult(int resultInt) {
    Token token = new Token(resultInt + "", TokenType.INT_LITERAL);
    ArrayList<Token> intToken = new ArrayList<Token>();
    intToken.add(token);
    IntLiteral intLiteral = new IntLiteral(intToken);

    ArrayList<Token> tokens = new ArrayList<Token>();
    tokens.add(intLiteral);
    return new Literal(tokens);
  }

  private Literal constructLiteralResult(boolean resultBoolean) {
    Token token = new Token(resultBoolean + "", TokenType.BOOLEAN_LITERAL);
    ArrayList<Token> booleanToken = new ArrayList<Token>();
    booleanToken.add(token);
    BooleanLiteral booleanLiteral = new BooleanLiteral(booleanToken);

    ArrayList<Token> tokens = new ArrayList<Token>();
    tokens.add(booleanLiteral);
    return new Literal(tokens);
  }
}
