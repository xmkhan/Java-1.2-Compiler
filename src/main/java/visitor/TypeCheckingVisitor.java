package visitor;

import exception.VisitorException;
import symbol.SymbolTable;
import token.*;
import java.util.Stack;

public class TypeCheckingVisitor extends BaseVisitor {

  private final SymbolTable symbolTable;
  public Stack<TypeCheckToken> tokenStack;

  public TypeCheckingVisitor(SymbolTable symbolTable) {
    this.symbolTable = symbolTable;
    tokenStack = new Stack<TypeCheckToken>();
  }

  @Override
  public void visit(Literal token) throws VisitorException {
    super.visit(token);
    tokenStack.push(new TypeCheckToken(token.getTokenType()));
  }

  @Override
  public void visit(ConditionalOrExpression token) throws VisitorException {
    super.visit(token);
    if(token.children.size() == 1) return;

    TokenType type1 = tokenStack.pop().tokenType;
    TokenType type2 = tokenStack.pop().tokenType;
    if(type1 == TokenType.BOOLEAN_LITERAL && type2 == TokenType.BOOLEAN_LITERAL) {
      tokenStack.push(new TypeCheckToken(TokenType.BOOLEAN_LITERAL));
    } else {
      throw new VisitorException("Boolean OR expression expected boolean || boolean but found " + type1.toString() + " || " + type2.toString(), token);
    }
  }

  @Override
  public void visit(InclusiveOrExpression token) throws VisitorException {
    super.visit(token);
    if(token.children.size() == 1) return;

    TokenType type1 = tokenStack.pop().tokenType;
    TokenType type2 = tokenStack.pop().tokenType;
    if(type1 == TokenType.BOOLEAN_LITERAL && type2 == TokenType.BOOLEAN_LITERAL) {
      tokenStack.push(new TypeCheckToken(TokenType.BOOLEAN_LITERAL));
    } else {
      throw new VisitorException("Boolean OR expression expected boolean | boolean but found " + type1.toString() + " | " + type2.toString(), token);
    }
  }

  @Override
  public void visit(AndExpression token) throws VisitorException {
    super.visit(token);
    if(token.children.size() == 1) return;

    TokenType type1 = tokenStack.pop().tokenType;
    TokenType type2 = tokenStack.pop().tokenType;
    if(type1 == TokenType.BOOLEAN_LITERAL && type2 == TokenType.BOOLEAN_LITERAL) {
      tokenStack.push(new TypeCheckToken(TokenType.BOOLEAN_LITERAL));
    } else {
      throw new VisitorException("Boolean OR expression expected boolean & boolean but found " + type1.toString() + " & " + type2.toString(), token);
    }
  }

  @Override
  public void visit(EqualityExpression token) throws VisitorException {
    super.visit(token);
    if(token.children.size() == 1) return;

    TypeCheckToken type1 = tokenStack.pop();
    TypeCheckToken type2 = tokenStack.pop();

    TokenType [] validTypes = {TokenType.BOOLEAN_LITERAL, TokenType.INT_LITERAL, TokenType.CHAR_LITERAL, TokenType.BYTE, TokenType.SHORT};

    if(type1.tokenType == TokenType.BOOLEAN_LITERAL && type2.tokenType == TokenType.BOOLEAN_LITERAL) {
      tokenStack.push(new TypeCheckToken(TokenType.BOOLEAN_LITERAL));
    } if(type1.tokenType == TokenType.CHAR_LITERAL && type2.tokenType == TokenType.BOOLEAN_LITERAL) {
      tokenStack.push(new TypeCheckToken(TokenType.BOOLEAN_LITERAL));
    }   else {
      throw new VisitorException("Boolean OR expression expected boolean & boolean but found " + type1.toString() + " & " + type2.toString(), token);
    }
  }
}
