package visitor;

import exception.TypeHierarchyException;
import exception.VisitorException;
import symbol.SymbolTable;
import token.*;
import type.hierarchy.HierarchyGraph;
import type.hierarchy.HierarchyGraphNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class TypeCheckingVisitor extends BaseVisitor {
  private enum OperandSide {LEFT, RIGHT};
  private final String STRING_CLASS_PATH = "java.lang.String";

  private final SymbolTable symbolTable;
  private final HierarchyGraph hierarchyGraph;
  public Stack<TypeCheckToken> tokenStack;
  private HierarchyGraphNode node;
  private CompilationUnit unit;

  public TypeCheckingVisitor(SymbolTable symbolTable, HierarchyGraph hierarchyGraph, HierarchyGraphNode n, CompilationUnit unit) {
    this.symbolTable = symbolTable;
    this.hierarchyGraph = hierarchyGraph;
    this.node = n;
    this.unit = unit;
    tokenStack = new Stack<TypeCheckToken>();
  }

  @Override
  public void visit(Literal token) throws VisitorException {
    super.visit(token);
    Token literal = token.children.get(0);

    TypeCheckToken literalToken = null;
    if(literal instanceof StringLiteral) {
      literalToken = createStringToken();
    } else if(literal instanceof IntLiteral) {
      literalToken = new TypeCheckToken(TokenType.INT);
    } else if(literal instanceof BooleanLiteral) {
      literalToken = new TypeCheckToken(TokenType.BOOLEAN);
    } else if(literal instanceof CharLiteral) {
      literalToken = new TypeCheckToken(TokenType.CHAR);
    } else {
      throw new VisitorException("Unexpected literal: " + token.getLexeme() + " of type " + token.getTokenType(), token);
    }

    tokenStack.push(literalToken);
  }

  @Override
  public void visit(ConditionalOrExpression token) throws VisitorException {
    super.visit(token);
    if(token.children.size() == 1) return;

    TypeCheckToken type1 = tokenStack.pop();
    TypeCheckToken type2 = tokenStack.pop();
    if(type1.tokenType == TokenType.BOOLEAN && type2.tokenType == TokenType.BOOLEAN && !type1.isArray && !type2.isArray) {
      tokenStack.push(new TypeCheckToken(TokenType.BOOLEAN));
    } else {
      throw new VisitorException("Boolean OR expression expected boolean || boolean but found " + type1.tokenType.toString() + " || " + type2.tokenType.toString(), token);
    }
  }

  @Override
  public void visit(ConditionalAndExpression token) throws VisitorException {
    super.visit(token);
    if(token.children.size() == 1) return;

    TypeCheckToken type1 = tokenStack.pop();
    TypeCheckToken type2 = tokenStack.pop();
    if(type1.tokenType == TokenType.BOOLEAN && type2.tokenType == TokenType.BOOLEAN && !type1.isArray && !type2.isArray) {
      tokenStack.push(new TypeCheckToken(TokenType.BOOLEAN));
    } else {
      throw new VisitorException("Boolean AND expression expected boolean && boolean but found " + type1.tokenType.toString() + " && " + type2.tokenType.toString(), token);
    }
  }

  @Override
  public void visit(InclusiveOrExpression token) throws VisitorException {
    super.visit(token);
    if(token.children.size() == 1) return;

    TypeCheckToken type1 = tokenStack.pop();
    TypeCheckToken type2 = tokenStack.pop();
    if(type1.tokenType == TokenType.BOOLEAN && type2.tokenType == TokenType.BOOLEAN && !type1.isArray && !type2.isArray) {
      tokenStack.push(new TypeCheckToken(TokenType.BOOLEAN));
    } else {
      throw new VisitorException("Boolean OR expression expected boolean | boolean but found " + type1.tokenType.toString() + " | " + type2.tokenType.toString(), token);
    }
  }

  @Override
  public void visit(AndExpression token) throws VisitorException {
    super.visit(token);
    if(token.children.size() == 1) return;

    TokenType type1 = tokenStack.pop().tokenType;
    TokenType type2 = tokenStack.pop().tokenType;
    if(type1 == TokenType.BOOLEAN && type2 == TokenType.BOOLEAN) {
      tokenStack.push(new TypeCheckToken(TokenType.BOOLEAN));
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

    TokenType [] validTypes = {TokenType.BOOLEAN, TokenType.INT, TokenType.CHAR, TokenType.BYTE, TokenType.SHORT};

    try {
      if(rightSide.isArray == leftSide.isArray && validType(rightSide.tokenType, validTypes) && rightSide.tokenType == leftSide.tokenType) {
        tokenStack.push(new TypeCheckToken(TokenType.BOOLEAN));
      }  else if(rightSide.isArray == leftSide.isArray && rightSide.tokenType == TokenType.OBJECT && leftSide.tokenType == TokenType.OBJECT &&
              hierarchyGraph.areNodesConnected(rightSide.getAbsolutePath(), leftSide.getAbsolutePath())) {
        tokenStack.push(new TypeCheckToken(TokenType.BOOLEAN));
      } else if (leftSide.tokenType == TokenType.OBJECT && rightSide.tokenType == TokenType.NULL) {
        tokenStack.push(new TypeCheckToken(TokenType.BOOLEAN));
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
      TypeCheckToken typeRightSide = tokenStack.pop();
      TypeCheckToken typeLeftSide = tokenStack.pop();

      if((!typeLeftSide.isArray && typeLeftSide.tokenType != TokenType.OBJECT)) {
        throw new VisitorException("InstanceOf expression expected Array|Object instanceOf Array|Object but found " + typeLeftSide + " instanceOf " + typeRightSide, token);
      }
      //TODO: Check object hierarchy
      tokenStack.push(new TypeCheckToken(TokenType.BOOLEAN));
    } else {
      TypeCheckToken rightType = tokenStack.pop();
      TypeCheckToken leftType = tokenStack.pop();
      TokenType[] validTypes = {TokenType.INT, TokenType.CHAR, TokenType.BYTE, TokenType.SHORT};

      if (!validTypes(rightType.tokenType, leftType.tokenType, validTypes)) {
        throw new VisitorException("Relational expression expected 'int|char|byte|short RelationalOperator int|char|byte|short' but found " + rightType + " RelationalOperator " + leftType, token);
      }
      assertNotArray(token, rightType, OperandSide.RIGHT, token.children.get(1).getLexeme());
      assertNotArray(token, leftType, OperandSide.LEFT, token.children.get(1).getLexeme());
      tokenStack.push(new TypeCheckToken(TokenType.BOOLEAN));
    }
  }

  @Override
  public void visit(AdditiveExpression token) throws  VisitorException {
    super.visit(token);
    if (token.children.size() == 1) return;

    TypeCheckToken rightSide = tokenStack.pop();
    TypeCheckToken leftSide = tokenStack.pop();

    assertNotArray(token, rightSide, OperandSide.RIGHT, token.children.get(1).getLexeme());
    assertNotArray(token, leftSide, OperandSide.LEFT, token.children.get(1).getLexeme());

    // These types could be used with the '+' and '-' operators and the result is an int
    TokenType[]  validMinusPlusTypes = {TokenType.SHORT, TokenType.INT, TokenType.BYTE, TokenType.CHAR};

    if (token.children.get(1).getTokenType() == TokenType.MINUS_OP) {
      if (validTypes(leftSide.tokenType, rightSide.tokenType, validMinusPlusTypes)) {
        tokenStack.push(new TypeCheckToken(TokenType.INT));
      } else {
        throw new VisitorException("AdditiveExpression expected 'short|int|byte|char - short|int|byte|char but found " + leftSide + " - " + rightSide, token);
      }
    } else if (token.children.get(1).getTokenType() == TokenType.PLUS_OP) {
      // TODO: Fix string
      TokenType[] validStringConcatTypes = {TokenType.SHORT, TokenType.INT, TokenType.BYTE, TokenType.CHAR, TokenType.NULL, TokenType.BOOLEAN};
      if (leftSide.tokenType == TokenType.OBJECT && leftSide.absolutePath.equals(STRING_CLASS_PATH) && validType(rightSide.tokenType, validStringConcatTypes) ||
        rightSide.tokenType == TokenType.OBJECT && leftSide.absolutePath.equals(STRING_CLASS_PATH) && validType(leftSide.tokenType, validStringConcatTypes)) {
        tokenStack.push(createStringToken());
      } else if (validTypes(leftSide.tokenType, rightSide.tokenType, validMinusPlusTypes)) {
        tokenStack.push(new TypeCheckToken(TokenType.INT));
      } else {
        throw new VisitorException("String concatenation found invalid types " + leftSide + " + " + rightSide, token);
      }
    }
  }

  @Override
  public void visit(MultiplicativeExpression token) throws VisitorException {
    super.visit(token);
    if (token.children.size() == 1) return;

    TypeCheckToken rightSide = tokenStack.pop();
    TypeCheckToken leftSide = tokenStack.pop();

    assertNotArray(token, rightSide, OperandSide.RIGHT, token.children.get(1).getLexeme());
    assertNotArray(token, leftSide, OperandSide.LEFT, token.children.get(1).getLexeme());

    TokenType[]  validUnaryExpressionTypes = {TokenType.SHORT, TokenType.INT, TokenType.BYTE, TokenType.CHAR};
    if (validTypes(leftSide.tokenType, rightSide.tokenType, validUnaryExpressionTypes)) {
      tokenStack.push(new TypeCheckToken(TokenType.INT));
    } else {
      throw new VisitorException("Expected short|int|byte|char " + token.children.get(1).getLexeme() +
        " short|int|byte|char' but found " + leftSide + " " + token.children.get(1).getLexeme() +
        " " + rightSide, token);
    }
  }

  @Override
  public void visit(UnaryExpression token) throws VisitorException {
    super.visit(token);
    if (token.children.size() == 1) return;

    TokenType type = tokenStack.peek().tokenType;
    TokenType[]  validUnaryExpressionTypes = {TokenType.SHORT, TokenType.INT, TokenType.BYTE, TokenType.CHAR};

    assertNotArray(token, tokenStack.peek(), OperandSide.RIGHT, token.children.get(0).getLexeme());
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

    TokenType [] validTypes = {TokenType.BOOLEAN, TokenType.INT, TokenType.CHAR, TokenType.BYTE, TokenType.SHORT};

    try {
      if (typeLeftSide.isArray == typeRightSide.isArray && validType(typeLeftSide.tokenType, validTypes) && typeLeftSide.tokenType == typeRightSide.tokenType) {
        tokenStack.push(typeLeftSide);
      } else if(typeLeftSide.isArray == false && typeRightSide.isArray == false &&
              typeLeftSide.isPrimitiveType() && typeRightSide.isPrimitiveType() &&
              isWideningPrimitiveConversion(typeRightSide.tokenType, typeLeftSide.tokenType)) {
        tokenStack.push(typeLeftSide);
      }
      else if (typeLeftSide.isArray == typeRightSide.isArray && typeLeftSide.tokenType == TokenType.OBJECT && typeRightSide.tokenType == TokenType.OBJECT &&
              hierarchyGraph.areNodesConnectedOneWay(typeRightSide.getAbsolutePath(), typeLeftSide.getAbsolutePath())) {
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

    if (token.children.size() == 2) {
      assertNotArray(token, tokenStack.peek(), OperandSide.RIGHT, token.children.get(0).getLexeme());
      if (type != TokenType.BOOLEAN) {
        throw new VisitorException("Unary operator '! UnaryExpression' was expecting UnaryExpression to be boolean but found " + type, token);
      }
    }
  }

  @Override
  public void visit(ConstructorDeclarator token) throws VisitorException {
    super.visit(token);
    if (!token.getIdentifier().equals(node.identifier)) {
      throw new VisitorException("Constructor name " + token.getIdentifier() + " does not match class name " + node.identifier, token);
    }
  }

  @Override
  public void visit(Expression token) throws VisitorException {
    tokenStack.pop();
  }

  @Override
  public void visit(ForInit token) throws VisitorException {
    tokenStack.pop();
  }

  @Override
  public void visit(ForUpdate token) throws VisitorException {
    tokenStack.pop();
  }

  @Override
  public void visit(ClassInstanceCreationExpression token) throws VisitorException {
    super.visit(token);

    List<TypeCheckToken> arguments = new ArrayList<TypeCheckToken>();
    for (int i = 0; i < token.argumentList.numArguments(); i++) {
      arguments.add(tokenStack.pop());
    }

    Name name = (Name) token.classType.children.get(0).children.get(0);
    if (hierarchyGraph.get(name.getAbsolutePath()).isAbstract()) {
      // ClassType was abstract
      throw new VisitorException("Abstract class " + " cannot be instantiated", token);
    }

    Class[] classes = new Class[1];
    classes[0] = ConstructorDeclaration.class;
    String constructor = name.getAbsolutePath() + "." + name.getLexeme();
    List<Token> matchingDeclarations = symbolTable.findWithPrefixOfAnyType(constructor, classes);

    boolean found = false;
    for (Token declaration : matchingDeclarations) {
      if(declaration instanceof ConstructorDeclaration) {
        ConstructorDeclaration constructorDeclaration = (ConstructorDeclaration) declaration;
        List<FormalParameter> parameters = constructorDeclaration.declarator.getParameterList().getFormalParameters();
        if(parameters.size() == arguments.size()) {
          boolean allParametersMatch = true;
          for(int i = 0; i < parameters.size(); i++) {
            FormalParameter constructorParameter = parameters.get(i);
            TypeCheckToken calledParameter = arguments.get(parameters.size() - i);
            if(constructorParameter.isPrimitive() && calledParameter.isPrimitiveType()
              && constructorParameter.isArray() == calledParameter.isArray) {
            } else if(constructorParameter.isReferenceType() && calledParameter.tokenType == TokenType.OBJECT
              && constructorParameter.isArray() == calledParameter.isArray &&
              constructorParameter.getAbsolutePath().equals(calledParameter.getAbsolutePath())) {
            } else {
              allParametersMatch = false;
              break;
            }
          }

          if(allParametersMatch) {
            found = true;
            break;
          }
        }
      }
    }

    if(found) {
      for(Declaration declaration : name.getDeclarationTypes()) {
        if(declaration instanceof ClassDeclaration) {
          tokenStack.push(new TypeCheckToken(declaration));
          break;
        }
      }
    } else {
      throw new VisitorException("Couldn't find any constructors for " + name.getAbsolutePath(), token);
    }
  }

  @Override
  public void visit(LocalVariableDeclaration token) throws VisitorException {
    tokenStack.pop();
  }

    @Override
  public void visit(ArrayAccess token) throws VisitorException {
    TypeCheckToken expressionType = tokenStack.pop();
    if (expressionType.tokenType != TokenType.INT) {
      throw new VisitorException("ArrayAccess requires an integer but found " + expressionType.tokenType.toString(), token);
    }

    if (token.children.get(0) instanceof Name) {
      Name name = token.name;

      Declaration determinedDecalaration = null;
      for (Declaration declaration : name.getDeclarationTypes()) {
        if(declaration instanceof FormalParameter ||
          declaration instanceof  FieldDeclaration ||
          declaration instanceof ClassDeclaration ||
          declaration instanceof LocalVariableDeclaration) {
          determinedDecalaration = declaration;
          break;
        }
      }

      if(determinedDecalaration != null) {
        tokenStack.push(new TypeCheckToken(determinedDecalaration));
      } else {
        throw new VisitorException("Failed to find declaration to for array type: " + name.getLexeme(), token);
      }
    } else {
      // Primary case: No need to do anything as Primary type is on top of the stack.
      // We check to make sure Expression is of type int at the top
    }
  }

  @Override
  public void visit(ArrayCreationExpression token) throws VisitorException {
    if(token.isPrimitiveType()) {
      TypeCheckToken expression = tokenStack.pop();
      TypeCheckToken primitive = tokenStack.pop();
      
    } else {

    }

    if (token.isPrimitiveType()) return;
    // call shah's function on token.classType....
    /*if (false) {
      // ClassType was abstract
      throw new VisitorException("Abstract class " + " cannot be instantiated", token);
    }*/
  }

  @Override
  public void visit(FieldAccess token) throws VisitorException {
    super.visit(token);
    TypeCheckToken firstIdentifier = tokenStack.pop();
    if (firstIdentifier.isArray) {
      if (token.identifier.equals("length")) {
        tokenStack.push(new TypeCheckToken(TokenType.INT));
        return;
      } else {
        throw new VisitorException("Invalid array field access for field: " + token.identifier.getLexeme(), token);
      }
    }

    List<Token> potentialFields = symbolTable.findWithPrefixOfAnyType(
        firstIdentifier.getAbsolutePath() + '.' + token.identifier.getLexeme(), new Class[] {FieldDeclaration.class});
    if (potentialFields == null || potentialFields.isEmpty()) {
      throw new VisitorException("No field could be resolved for field: " + token.identifier.getLexeme(), token);
    }
    tokenStack.push(new TypeCheckToken((Declaration)potentialFields.get(0)));
  }

  @Override
  public void visit(LeftHandSide token) throws VisitorException {
    super.visit(token);
    if(token.children.get(0).getTokenType() == TokenType.Name) {
      Name name = (Name) token.children.get(0);
      Declaration determinedDecalaration = null;
      for (Declaration declaration : name.getDeclarationTypes()) {
        if(declaration instanceof LocalVariableDeclaration ||
                declaration instanceof  FieldDeclaration ||
                declaration instanceof FormalParameter) {
          determinedDecalaration = declaration;
          break;
        }
      }

      if(determinedDecalaration != null) {
        tokenStack.push(new TypeCheckToken(determinedDecalaration));
      } else {
        throw new VisitorException("Failed to find declaration to assign to", token);
      }
    }
  }

  @Override
  public void visit(CastExpression token) throws VisitorException {
    super.visit(token);

    TypeCheckToken tokenToCast = tokenStack.pop();
    if(tokenToCast.isArray != token.isArrayCast()) {
      throw new VisitorException("Expected both to be array or not array but found type is array: " + tokenToCast.isArray + " and type is array: " + token.isArrayCast(), token);
    }

    // TODO: handle edge cases for casts
    try {
      // Handle boolean case
      if (tokenToCast.isPrimitiveType() && token.isPrimitiveType() && tokenToCast.tokenType == token.primitiveType.getType().getTokenType()) {
        tokenStack.push(new TypeCheckToken(token.getTokenType(), token.isArrayCast()));
      } else if (tokenToCast.isPrimitiveType() && token.isPrimitiveType() &&
              (isWideningPrimitiveConversion(tokenToCast.tokenType, token.primitiveType.getType().getTokenType()) ||
               isNarrowingPrimitiveConversion(tokenToCast.tokenType, token.primitiveType.getType().getTokenType()))) {
        tokenStack.push(new TypeCheckToken(token.getTokenType(), token.isArrayCast()));
      } else if(tokenToCast.tokenType == TokenType.OBJECT && token.isName() && tokenToCast.getAbsolutePath().equals(token.name.getAbsolutePath())) {
          tokenStack.push(new TypeCheckToken(token.getTokenType(), token.isArrayCast()));
      } else if (tokenToCast.tokenType == TokenType.OBJECT && token.isName() && hierarchyGraph.areNodesConnected(token.name.getAbsolutePath(), tokenToCast.getAbsolutePath())) {
        tokenStack.push(new TypeCheckToken(token.getTokenType(), token.isArrayCast()));
      } else if(token.isName() && tokenToCast.tokenType == TokenType.NULL) {
        tokenStack.push(new TypeCheckToken(token.getTokenType(), token.isArrayCast()));
      }
      else {
        throw new VisitorException("Not castable types: "  + token.toString() + " and " + tokenToCast.toString(), token);
      }
    } catch(TypeHierarchyException e) {
      throw new VisitorException(e.getMessage(), token);
    }
  }

  @Override
  public void visit(Primary token) throws VisitorException {
    super.visit(token);

    if (token.children.get(0).getTokenType() == TokenType.THIS) {
      tokenStack.push(new TypeCheckToken((unit.typeDeclaration.getDeclaration())));
    }
  }

  @Override
  public void visit(MethodInvocation token) throws VisitorException {
    super.visit(token);

    List<TypeCheckToken> arguments = new ArrayList<TypeCheckToken>();
    for (int i = 0; i < token.argumentList.numArguments(); i++) {
      arguments.add(tokenStack.pop());
    }

    if(token.isOnPrimary()) {

    } else if(token.name != null) {
      Name name = (Name) token.name;

    }
  }

  private TypeCheckToken createStringToken() {
    TypeCheckToken stringType = new TypeCheckToken(TokenType.OBJECT);
    stringType.isArray = false;
    stringType.absolutePath = "java.lang.String";
    return stringType;
  }



  private boolean isWideningPrimitiveConversion(TokenType from, TokenType to) {
    if(from == TokenType.BYTE) {
      return to == TokenType.INT | to == TokenType.SHORT;
    } else if(from == TokenType.SHORT) {
      return to == TokenType.INT;
    } else if(from == TokenType.CHAR) {
      return to == TokenType.INT;
    } else if(from == TokenType.INT) {
      return false;
    } else {
      return false;
    }
  }

  private boolean isNarrowingPrimitiveConversion(TokenType from, TokenType to) {
    if(from == TokenType.BYTE) {
      return to == TokenType.CHAR;
    } else if(from == TokenType.SHORT) {
      return to == TokenType.BYTE || to == TokenType.CHAR;
    } else if(from == TokenType.CHAR) {
      return to == TokenType.BYTE || to == TokenType.SHORT;
    } else if(from == TokenType.INT) {
      return to == TokenType.BYTE || to == TokenType.SHORT || to == TokenType.CHAR;
    } else {
      return false;
    }
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

  private void assertNotArray(Token token, TypeCheckToken type, OperandSide side, String operator) throws VisitorException {
    if (type.isArray) {
      throw new VisitorException("Arrays are not allowed to be used with the " + operator + " operator.  The " + side.toString().toLowerCase() + " hand operator is an array of type " + type.tokenType, token);
    }
  }

  private boolean ensureExtendParentHasDefaultConstructor() {
    if (node.extendsList.size() == 0) return true;
    return node.extendsList.get(0).isDefaultConstructorVisibleToChildren();
  }
}