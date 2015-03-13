package visitor;

import exception.CompilerException;
import exception.TypeHierarchyException;
import exception.VisitorException;
import org.omg.CORBA.portable.ValueInputStream;
import symbol.SymbolTable;
import token.*;
import type.hierarchy.HierarchyGraph;

import java.util.Stack;

public class TypeCheckingVisitor extends BaseVisitor {

  private final SymbolTable symbolTable;
  private final HierarchyGraph hierarchyGraph;
  public Stack<TypeCheckToken> tokenStack;

  public TypeCheckingVisitor(SymbolTable symbolTable, HierarchyGraph hierarchyGraph) {
    this.symbolTable = symbolTable;
    this.hierarchyGraph = hierarchyGraph;
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
  public void visit(ConditionalAndExpression token) throws VisitorException {
    super.visit(token);
    if(token.children.size() == 1) return;

    TokenType type1 = tokenStack.pop().tokenType;
    TokenType type2 = tokenStack.pop().tokenType;
    if(type1 == TokenType.BOOLEAN_LITERAL && type2 == TokenType.BOOLEAN_LITERAL) {
      tokenStack.push(new TypeCheckToken(TokenType.BOOLEAN_LITERAL));
    } else {
      throw new VisitorException("Boolean AND expression expected boolean && boolean but found " + type1.toString() + " && " + type2.toString(), token);
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

    TypeCheckToken rightSide = tokenStack.pop();
    TypeCheckToken leftSide = tokenStack.pop();

    TokenType [] validTypes = {TokenType.BOOLEAN_LITERAL, TokenType.INT_LITERAL, TokenType.CHAR_LITERAL, TokenType.BYTE, TokenType.SHORT};

    try {
      if(rightSide.isArray == leftSide.isArray && validType(rightSide.tokenType, validTypes) && rightSide.tokenType == leftSide.tokenType) {
        tokenStack.push(new TypeCheckToken(TokenType.BOOLEAN_LITERAL));
      } else if(rightSide.isArray == leftSide.isArray && rightSide.tokenType == TokenType.OBJECT && leftSide.tokenType == TokenType.OBJECT &&
              hierarchyGraph.areNodesConnected(rightSide.declaration.getAbsolutePath(), leftSide.declaration.getAbsolutePath())) {
        tokenStack.push(new TypeCheckToken(TokenType.BOOLEAN_LITERAL));
      } else if (leftSide.tokenType == TokenType.OBJECT && rightSide.tokenType == TokenType.NULL) {
        tokenStack.push(new TypeCheckToken(TokenType.BOOLEAN_LITERAL));
      } else {
        throw new VisitorException("Boolean Equality expression expected both of same inherited type but found " + rightSide.toString() + " & " + leftSide.toString(), token);
      }
    } catch(TypeHierarchyException e) {
      throw new VisitorException(e.getMessage(), token);
    }
  }

  @Override
  public void visit(RelationalExpression token) throws VisitorException {
    super.visit(token);
    if (token.children.size() == 1) return;

    if (token.children.get(1).getTokenType() == TokenType.INSTANCEOF) {
      TokenType typeRightSide = tokenStack.pop().tokenType;
      TokenType typeLeftSide = tokenStack.pop().tokenType;
      if (!(typeLeftSide == TokenType.ArrayType || typeLeftSide == TokenType.OBJECT)) {
        throw new VisitorException("InstanceOf expression expected Array|Object instanceOf Array|Object but found " + typeLeftSide + " instanceOf " + typeRightSide, token);
      }
      tokenStack.push(new TypeCheckToken(TokenType.BOOLEAN));
    } else {
      TokenType type1 = tokenStack.pop().tokenType;
      TokenType type2 = tokenStack.pop().tokenType;
      TokenType[] validTypes = new TokenType[]{TokenType.INT, TokenType.CHAR, TokenType.BYTE, TokenType.SHORT};

      if (!validTypes(type1, type2, validTypes)) {
        throw new VisitorException("Relational expression expected 'int|char|byte|short RelationalOperator int|char|byte|short' but found " + type1 + " RelationalOperator " + type2, token);
      }
      tokenStack.push(new TypeCheckToken(TokenType.BOOLEAN));
    }
  }

  @Override
  public void visit(AdditiveExpression token) throws  VisitorException {
    super.visit(token);
    if (token.children.size() == 1) return;

    TokenType typeRightSide = tokenStack.pop().tokenType;
    TokenType typeLeftSide = tokenStack.pop().tokenType;

    // These types could be used with the '+' and '-' operators and the result is an int
    TokenType[]  validMinusPlusTypes = new TokenType[]{TokenType.SHORT, TokenType.INT, TokenType.BYTE, TokenType.CHAR};

    if (token.children.get(1).getTokenType() == TokenType.MINUS_OP) {
      if (validTypes(typeLeftSide, typeRightSide, validMinusPlusTypes)) {
        tokenStack.push(new TypeCheckToken(TokenType.INT));
      } else {
        throw new VisitorException("AdditiveExpression expected 'short|int|byte|char - short|int|byte|char but found " + typeLeftSide + " - " + typeRightSide, token);
      }
    } else if (token.children.get(1).getTokenType() == TokenType.PLUS_OP) {
      TokenType[] validStringConcatTypes = new TokenType[]{TokenType.SHORT, TokenType.INT, TokenType.BYTE, TokenType.CHAR, TokenType.NULL, TokenType.BOOLEAN, TokenType.STR_LITERAL};
      if (typeLeftSide == TokenType.STR_LITERAL && validType(typeRightSide, validStringConcatTypes) ||
        typeRightSide == TokenType.STR_LITERAL && validType(typeLeftSide, validStringConcatTypes)) {
        tokenStack.push(new TypeCheckToken(TokenType.STR_LITERAL));
      } else if (validTypes(typeLeftSide, typeRightSide, validMinusPlusTypes)) {
        tokenStack.push(new TypeCheckToken(TokenType.INT));
      } else {
        throw new VisitorException("String concatenation found invalid types " + typeLeftSide + " + " + typeRightSide, token);
      }
    }
  }

  @Override
  public void visit(MultiplicativeExpression token) throws VisitorException {
    super.visit(token);
    if (token.children.size() == 1) return;

    TokenType typeRightSide = tokenStack.pop().tokenType;
    TokenType typeLeftSide = tokenStack.pop().tokenType;

    TokenType[]  validUnaryExpressionTypes = new TokenType[]{TokenType.SHORT, TokenType.INT, TokenType.BYTE, TokenType.CHAR};
    if (validTypes(typeLeftSide, typeRightSide, validUnaryExpressionTypes)) {
      tokenStack.push(new TypeCheckToken(TokenType.INT));
    } else {
      throw new VisitorException("Expected short|int|byte|char " + token.children.get(1).getLexeme() +
        " short|int|byte|char' but found " + typeLeftSide + " " + token.children.get(1).getLexeme() +
        " " + typeRightSide, token);
    }
  }

  @Override
  public void visit(UnaryExpression token) throws VisitorException {
    super.visit(token);
    if (token.children.size() == 1) return;

    TokenType type = tokenStack.peek().tokenType;
    TokenType[]  validUnaryExpressionTypes = new TokenType[]{TokenType.SHORT, TokenType.INT, TokenType.BYTE, TokenType.CHAR};

    if (!validType(type, validUnaryExpressionTypes)) {
      throw new VisitorException("Unary operator '- UnaryExpression' was expecting UnaryExpression to be of type short|int|byte|char but found " + type, token);
    } else {
      // we just peeked the stack so no need to push the type back on the stack again
    }
  }

  public void visit(Assignment token) throws VisitorException {
    super.visit(token);

    TypeCheckToken typeRightSide = tokenStack.pop();
    TypeCheckToken typeLeftSide = tokenStack.pop();

    TokenType [] validTypes = {TokenType.BOOLEAN_LITERAL, TokenType.INT_LITERAL, TokenType.CHAR_LITERAL, TokenType.BYTE, TokenType.SHORT};

    try {
      if (typeLeftSide.isArray == typeRightSide.isArray && validType(typeLeftSide.tokenType, validTypes) && typeLeftSide.tokenType == typeRightSide.tokenType) {
        tokenStack.push(typeLeftSide);
      } else if (typeLeftSide.isArray == typeRightSide.isArray && typeLeftSide.tokenType == TokenType.OBJECT && typeRightSide.tokenType == TokenType.OBJECT &&
              hierarchyGraph.areNodesConnectedOneWay(typeRightSide.declaration.getAbsolutePath(), typeLeftSide.declaration.getAbsolutePath())) {
        tokenStack.push(typeLeftSide);
      } else if (typeLeftSide.tokenType == TokenType.OBJECT && typeRightSide.tokenType == TokenType.NULL) {
        tokenStack.push(typeLeftSide);
      } else {
        throw new VisitorException("Assignment should be of same type or from parent to subclass.  Found: " + typeRightSide.toString() + " being assigned to "  + typeLeftSide.toString(), token);
      }
    } catch (TypeHierarchyException e) {
      throw new VisitorException(e.getMessage(), token);
    }
  }

  @Override
  public void visit(UnaryExpressionNotMinus token) throws VisitorException {
    super.visit(token);
    if (token.children.get(0).getTokenType() == TokenType.Primary ||
      token.children.get(0).getTokenType() == TokenType.Name) return;

    // No need to pop since if the type is valid we would've to push it back on the stack anyways
    TokenType type = tokenStack.peek().tokenType;

    if (token.children.size() == 2 && type != TokenType.BOOLEAN) {
      throw new VisitorException("Unary operator '! UnaryExpression' was expecting UnaryExpression to be boolean but found " + type, token);
    }
  }

  @Override
  public void visit(LeftHandSide token) throws VisitorException {
    super.visit(token);
    if(token.children.get(0).getTokenType() == TokenType.Name) {
      // call shahs code and push
    }
  }

  @Override
  public void visit(CastExpression token) throws VisitorException {
    super.visit(token);
  }

  public void visit(Primary token) throws VisitorException {
    super.visit(token);
    if (token.children.get(0).getTokenType() != TokenType.THIS) return;


  }

  private boolean validTypes(TokenType type1, TokenType type2, TokenType[] types) {
    return validType(type1, types) && validType(type2, types);
  }

  private boolean validType(TokenType type, TokenType[] types) {
    for (TokenType validType : types) {
      if (validType == type) return true;
    }
    return false;
  }
}
