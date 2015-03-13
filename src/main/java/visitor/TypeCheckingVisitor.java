package visitor;

import exception.VisitorException;
import symbol.SymbolTable;
import token.*;

import java.util.Stack;

public class TypeCheckingVisitor extends VariableScopeVisitor {

  public Stack<Token> tokenStack;

  public TypeCheckingVisitor(SymbolTable symbolTable) {
    super(symbolTable);
    tokenStack = new Stack<Token>();
  }

  @Override
  public void visit(Literal token) throws VisitorException {
    super.visit(token);
    tokenStack.push(token);
  }

  @Override
  public void visit(Name token) throws VisitorException {
    super.visit(token);
    // Call Shahs code and push type
  }

  @Override
  public void visit(ConditionalOrExpression token) throws VisitorException {
    super.visit(token);
  }


}
