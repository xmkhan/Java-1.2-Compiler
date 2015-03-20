package visitor;

import exception.VisitorException;
import token.*;

/**
 * Visits stmts to figure out if it can get in and out of stmts.
 */
public class ReachabilityVisitor extends BaseVisitor {

  @Override
  public void visit(IfThenStatement stmt1) throws VisitorException {
    super.visit(stmt1);
    BaseStatement stmt2 = (BaseStatement) stmt1.children.get(4);
    stmt2.in = stmt1.in;
    stmt1.out = stmt1.in;

    assertStatementEnterable(stmt2);
  }

  @Override
  public void visit(IfThenElseStatement stmt1) throws VisitorException {
    super.visit(stmt1);
    BaseStatement stmt2 = (BaseStatement) stmt1.children.get(4);
    BaseStatement stmt3 = (BaseStatement) stmt1.children.get(6);
    stmt2.in = stmt3.in = stmt1.in;
    stmt1.out = stmt2.out | stmt3.out;

    assertStatementEnterable(stmt2);
  }

  @Override
  public void visit(IfThenElseStatementNoShortIf stmt1) throws VisitorException {
    super.visit(stmt1);
    BaseStatement stmt2 = (BaseStatement) stmt1.children.get(4);
    BaseStatement stmt3 = (BaseStatement) stmt1.children.get(6);
    stmt2.in = stmt3.in = stmt1.in;
    assertStatementEnterable(stmt2);
  }

  @Override
  public void visit(WhileStatement stmt1) throws VisitorException {
    super.visit(stmt1);
    BaseStatement stmt2 = (BaseStatement) stmt1.children.get(4);
    Token evaluatedToken = stmt1.children.get(2);

    CheckLookReachable(evaluatedToken, stmt1, stmt2);
  }

  @Override
  public void visit(WhileStatementNoShortIf stmt1) throws VisitorException {
    super.visit(stmt1);
    BaseStatement stmt2 = (BaseStatement) stmt1.children.get(4);
    Token evaluatedToken = stmt1.children.get(2);

    CheckLookReachable(evaluatedToken, stmt1, stmt2);
  }

  @Override
  public void visit(BlockStatements token) throws VisitorException {
    super.visit(token);

    token.blockStatements.get(0).in = token.in;
    for(int a = 1; a < token.blockStatements.size(); a++) {
      token.blockStatements.get(a).in = token.blockStatements.get(a - 1).out;
      assertStatementEnterable(token.blockStatements.get(a));
    }
    token.out = token.blockStatements.get(token.blockStatements.size() - 1).out;
  }

  @Override
  public void visit(MethodBody token) throws VisitorException {
    super.visit(token);
    if(!token.isEmpty()) {
      BaseStatement block = (BaseStatement) token.children.get(0);
      block.in = true;
    }
  }

  @Override
  public void visit(Block token) throws VisitorException {
    super.visit(token);
    if(token.children.size() == 2) return;

    BlockStatements blockStatements = (BlockStatements) token.children.get(1);
    blockStatements.in = token.in;
    assertStatementEnterable(blockStatements);
  }

  @Override
  public void visit(ReturnStatement stmt1) throws VisitorException {
    super.visit(stmt1);
    stmt1.out = false;
  }

  @Override
  public void visit(ForStatement stmt1) throws VisitorException {
    super.visit(stmt1);
    BaseStatement stmt2 = stmt1.statement;
    Token evaluatedToken = stmt1.expression;

    CheckLookReachable(evaluatedToken, stmt1, stmt2);
  }

  @Override
  public void visit(ForStatementNoShortIf stmt1) throws VisitorException {
    super.visit(stmt1);
    BaseStatement stmt2 = stmt1.statement;
    Token evaluatedToken = stmt1.expression;

    CheckLookReachable(evaluatedToken, stmt1, stmt2);
  }

  public void CheckLookReachable(Token evaluation, BaseStatement currentStatement, BaseStatement statementBeingEntered) throws VisitorException{
    if(evaluation == null) {
      statementBeingEntered.in = currentStatement.in;
      currentStatement.out = currentStatement.in;

      assertStatementEnterable(statementBeingEntered);
      return;
    }

   try {
      StaticEvaluationVisitor evaluationVisitor = new StaticEvaluationVisitor();
      evaluation.accept(evaluationVisitor);

      if (evaluationVisitor.getLastValue()) {
        statementBeingEntered.in = currentStatement.in;
        currentStatement.out = false;
      } else {
        statementBeingEntered.in = false;
        currentStatement.out = currentStatement.in;
      }
    } catch(VisitorException e) {
      statementBeingEntered.in = currentStatement.in;
      currentStatement.out = currentStatement.in;
    }

    assertStatementEnterable(statementBeingEntered);
  }

  public void assertStatementEnterable(BaseStatement statement) throws VisitorException {
    if(statement.in == false) {
      throw new VisitorException("Statement can not be reached", statement);
    }
  }
}
