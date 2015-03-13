package visitor;

import exception.VisitorException;
import symbol.SymbolTable;
import token.*;

import java.util.ArrayList;
import java.util.Stack;

public class TypeCheckingVisitor extends VariableScopeVisitor {

  public Stack<TokenType> tokenStack;

  public TypeCheckingVisitor(SymbolTable symbolTable) {
    super(symbolTable);
    tokenStack = new Stack<TokenType>();
  }

  @Override
  public void visit(Literal token) throws VisitorException {
    super.visit(token);
    tokenStack.push(token.getTokenType());
  }

  @Override
  public void visit(Name token) throws VisitorException {
    super.visit(token);
    // Call Shahs code and push type
  }

  @Override
  public void visit(ConditionalOrExpression token) throws VisitorException {
    super.visit(token);
    TokenType type1 = tokenStack.pop();
    TokenType type2 = tokenStack.pop();
    if(type1 == TokenType.BOOLEAN_LITERAL && type2 == TokenType.BOOLEAN_LITERAL) {
      tokenStack.push(TokenType.BOOLEAN_LITERAL);
    } else {
      throw new VisitorException("Boolean OR expression expected boolean || boolean but found " + type1.toString() + " || " + type2.toString(), token);
    }
  }

}
