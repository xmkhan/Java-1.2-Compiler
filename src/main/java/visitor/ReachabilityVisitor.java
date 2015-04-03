package visitor;

import exception.ReachabilityVisitorException;
import exception.StaticEvaluationVisitorException;
import exception.VisitorException;
import token.*;

import java.util.List;

/**
 * Visits stmts to figure out if it can get in and out of stmts.
 */
public class ReachabilityVisitor extends BaseVisitor {

  private enum MODE { MODE_IN, MODE_OUT}

  private MODE mode;

  public void checkReachability(List<CompilationUnit> units) throws VisitorException{
    mode = MODE.MODE_IN;
    for (CompilationUnit unit : units) {
      unit.acceptReverse(this);
    }
    mode = MODE.MODE_OUT;
    for (CompilationUnit unit : units) {
      unit.accept(this);
    }
  }

  @Override
  public void visit(IfThenStatement stmt1) throws VisitorException {
    super.visit(stmt1);
    BaseStatement stmt2 = (BaseStatement) stmt1.children.get(4);
    switch (mode) {
      case MODE_IN:
        stmt2.in = stmt1.in;
        assertStatementEnterable(stmt2);
        break;
      case MODE_OUT:
        stmt1.out = stmt1.in;
        break;
    }
  }

  @Override
  public void visit(IfThenElseStatement stmt1) throws VisitorException {
    super.visit(stmt1);
    BaseStatement stmt2 = (BaseStatement) stmt1.children.get(4);
    BaseStatement stmt3 = (BaseStatement) stmt1.children.get(6);
    switch (mode) {
      case MODE_IN:
        stmt2.in = stmt3.in = stmt1.in;
        assertStatementEnterable(stmt2);
        break;
      case MODE_OUT:
        stmt1.out = stmt2.out | stmt3.out;
        break;
    }
  }

  @Override
  public void visit(IfThenElseStatementNoShortIf stmt1) throws VisitorException {
    super.visit(stmt1);
    BaseStatement stmt2 = (BaseStatement) stmt1.children.get(4);
    BaseStatement stmt3 = (BaseStatement) stmt1.children.get(6);
    switch (mode) {
      case MODE_IN:
        stmt2.in = stmt3.in = stmt1.in;
        assertStatementEnterable(stmt2);
        break;
      case MODE_OUT:
        stmt1.out = stmt2.out | stmt3.out;
        break;
    }
  }

  @Override
  public void visit(WhileStatement stmt1) throws VisitorException {
    super.visit(stmt1);
    BaseStatement stmt2 = (BaseStatement) stmt1.children.get(4);
    Token evaluatedToken = stmt1.children.get(2);

    CheckLoopReachable(evaluatedToken, stmt1, stmt2);
  }

  @Override
  public void visit(BlockStatement stmt1) throws VisitorException {
    super.visit(stmt1);
    propagate(stmt1);
  }

  @Override
  public void visit(StatementWithoutTrailingSubstatement stmt1) throws VisitorException {
    super.visit(stmt1);
    propagate(stmt1);
  }

  @Override
  public void visit(Statement stmt1) throws VisitorException {
    super.visit(stmt1);
    propagate(stmt1);
  }

  @Override
  public void visit(StatementNoShortIf stmt1) throws VisitorException {
    super.visit(stmt1);
    propagate(stmt1);
  }

  private void propagate(BaseStatement stmt1) {
    BaseStatement stmt2 = (BaseStatement) stmt1.children.get(0);
    switch (mode) {
      case MODE_IN:
        stmt2.in = stmt1.in;
        break;
      case MODE_OUT:
        stmt1.out = stmt2.out;
        break;
    }
  }

  @Override
  public void visit(WhileStatementNoShortIf stmt1) throws VisitorException {
    super.visit(stmt1);
    BaseStatement stmt2 = (BaseStatement) stmt1.children.get(4);
    Token evaluatedToken = stmt1.children.get(2);

    CheckLoopReachable(evaluatedToken, stmt1, stmt2);
  }

  @Override
  public void visit(BlockStatements token) throws VisitorException {
    super.visit(token);

    switch (mode) {
      case MODE_IN:
        token.blockStatements.get(0).in = token.in;
        assertStatementEnterable(token.blockStatements.get(0));
        break;
      case MODE_OUT:
        for(int a = 1; a < token.blockStatements.size(); a++) {
          token.blockStatements.get(a).in = token.blockStatements.get(a - 1).out;
          assertStatementEnterable(token.blockStatements.get(a));
        }
        token.out = token.blockStatements.get(token.blockStatements.size() - 1).out;
        break;
    }
  }

  @Override
  public void visit(MethodBody token) throws VisitorException {
    super.visit(token);
    if(!token.isEmpty()) {
      BaseStatement block = (BaseStatement) token.children.get(0);
      switch (mode) {
        case MODE_IN:
          block.in = true;
          break;
      }
    }
  }

  @Override
  public void visit(MethodDeclaration token) throws VisitorException {
    super.visit(token);

    switch (mode) {
      case MODE_IN:
        boolean containsNative = token.methodHeader.modifiers != null &&
            token.methodHeader.modifiers.containsModifier("native");
        boolean containsAbstract = token.methodHeader.modifiers != null &&
            token.methodHeader.modifiers.containsModifier("abstract");

        if (!token.methodHeader.isVoid() && token.methodBody.isEmpty() && !containsNative && !containsAbstract) {
          throw new ReachabilityVisitorException("Missing return elseStatement", token);
        }
      break;
      case MODE_OUT:
        if (!token.methodHeader.isVoid() && !token.methodBody.isEmpty() && token.methodBody.block.out) {
          throw new ReachabilityVisitorException("Non-executed return elseStatement", token);
        }
      break;
    }
  }

  @Override
  public void visit(Block token) throws VisitorException {
    super.visit(token);
    if(token.children.size() == 2) return;

    BlockStatements blockStatements = (BlockStatements) token.children.get(1);
    switch (mode) {
      case MODE_IN:
        blockStatements.in = token.in;
        assertStatementEnterable(blockStatements);
        break;
      case MODE_OUT:
        token.out = blockStatements.out;
        break;
    }
  }

  @Override
  public void visit(ReturnStatement stmt1) throws VisitorException {
    super.visit(stmt1);
    switch (mode) {
      case MODE_IN:
        break;
      case MODE_OUT:
        stmt1.out = false;
        break;
    }
  }

  @Override
  public void visit(ForStatement stmt1) throws VisitorException {
    super.visit(stmt1);
    BaseStatement stmt2 = stmt1.statement;
    Token evaluatedToken = stmt1.expression;

    CheckLoopReachable(evaluatedToken, stmt1, stmt2);
  }

  @Override
  public void visit(ForStatementNoShortIf stmt1) throws VisitorException {
    super.visit(stmt1);
    BaseStatement stmt2 = stmt1.statement;
    Token evaluatedToken = stmt1.expression;

    CheckLoopReachable(evaluatedToken, stmt1, stmt2);
  }



  public void CheckLoopReachable(Token evaluation, BaseStatement currentStatement, BaseStatement statementBeingEntered) throws VisitorException{
    if(evaluation == null) {
      switch (mode) {
        case MODE_IN:
          statementBeingEntered.in = currentStatement.in;
          assertStatementEnterable(statementBeingEntered);
          break;
        case MODE_OUT:
          currentStatement.out = false;
          break;
      }
      return;
    }

   try {
      StaticEvaluationVisitor evaluationVisitor = new StaticEvaluationVisitor();
      evaluation.accept(evaluationVisitor);

      if (evaluationVisitor.getLastValue()) {
        switch (mode) {
          case MODE_IN:
            statementBeingEntered.in = currentStatement.in;
            break;
          case MODE_OUT:
            currentStatement.out = false;
            break;
        }
      } else {
        switch (mode) {
          case MODE_IN:
            statementBeingEntered.in = false;
            break;
          case MODE_OUT:
            currentStatement.out = currentStatement.in;
            break;
        }
      }
    } catch(StaticEvaluationVisitorException e) {
     switch (mode) {
       case MODE_IN:
         statementBeingEntered.in = currentStatement.in;
         break;
       case MODE_OUT:
         currentStatement.out = currentStatement.in;
         break;
     }
    }

    assertStatementEnterable(statementBeingEntered);
  }

  public void assertStatementEnterable(BaseStatement statement) throws VisitorException {
    if(statement.in == false) {
      throw new ReachabilityVisitorException("Statement can not be reached", statement);
    }
  }
}
