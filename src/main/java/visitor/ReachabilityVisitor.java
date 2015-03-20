package visitor;

import exception.VisitorException;
import token.BaseStatement;
import token.ForStatement;
import token.ForStatementNoShortIf;
import token.ForUpdate;
import token.IfThenElseStatement;
import token.IfThenElseStatementNoShortIf;
import token.IfThenStatement;
import token.WhileStatement;
import token.WhileStatementNoShortIf;

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
  }

  @Override
  public void visit(IfThenElseStatement stmt1) throws VisitorException {
    super.visit(stmt1);
    BaseStatement stmt2 = (BaseStatement) stmt1.children.get(4);
    BaseStatement stmt3 = (BaseStatement) stmt1.children.get(6);
    stmt2.in = stmt3.in = stmt1.in;
    stmt1.out = stmt2.out | stmt3.out;
  }

  @Override
  public void visit(IfThenElseStatementNoShortIf stmt1) throws VisitorException {
    super.visit(stmt1);
    BaseStatement stmt2 = (BaseStatement) stmt1.children.get(4);
    BaseStatement stmt3 = (BaseStatement) stmt1.children.get(6);
    stmt2.in = stmt3.in = stmt1.in;
  }

  @Override
  public void visit(WhileStatement stmt1) throws VisitorException {
    super.visit(stmt1);
  }

  @Override
  public void visit(WhileStatementNoShortIf stmt1) throws VisitorException {
    super.visit(stmt1);
  }

  @Override
  public void visit(ForStatement token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(ForStatementNoShortIf token) throws VisitorException {
    super.visit(token);
  }
}
