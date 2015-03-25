package visitor;

import exception.TypeHierarchyException;
import exception.VisitorException;
import symbol.SymbolTable;
import token.*;
import type.hierarchy.HierarchyGraph;
import type.hierarchy.HierarchyGraphNode;

import java.util.*;

public class TypeCheckingVisitor extends BaseVisitor {
  private enum OperandSide {LEFT, RIGHT};
  private final String STRING_CLASS_PATH = "java.lang.String";
  private final String SERIALIZABLE_CLASS_PATH = "java.io.Serializable";
  private final String OBJECT_CLASS_PATH = "java.lang.Object";

  private final SymbolTable symbolTable;
  private final Map<CompilationUnit, HierarchyGraphNode> compilationUnitToNode;
  private final HierarchyGraph hierarchyGraph;
  public Stack<TypeCheckToken> tokenStack;
  private HierarchyGraphNode node;
  private CompilationUnit unit;

  public TypeCheckingVisitor(SymbolTable symbolTable, HierarchyGraph hierarchyGraph, Map<CompilationUnit, HierarchyGraphNode> compilationUnitToNode) {
    this.symbolTable = symbolTable;
    this.compilationUnitToNode = compilationUnitToNode;
    this.hierarchyGraph = hierarchyGraph;
  }

  public void typeCheckUnits(List<CompilationUnit> units) throws VisitorException {
    for(CompilationUnit unit : units) {
      // we don't need to type check interfaces
      if (unit.typeDeclaration.classDeclaration == null) {
        continue;
      }

      tokenStack = new Stack<TypeCheckToken>();
      this.unit = unit;
      this.node = compilationUnitToNode.get(unit);
      unit.accept(this);
    }
  }

  @Override
  public void visit(Literal token) throws VisitorException {
    super.visit(token);
    Token literal = token.getLiteral();

    TypeCheckToken literalToken = null;
    switch (literal.getTokenType()) {
      case STR_LITERAL:
        literalToken = createStringToken();
        break;
      case INT_LITERAL:
        literalToken = new TypeCheckToken(TokenType.INT);
        break;
      case BOOLEAN_LITERAL:
        literalToken = new TypeCheckToken(TokenType.BOOLEAN);
        break;
      case CHAR_LITERAL:
        literalToken = new TypeCheckToken(TokenType.CHAR);
        break;
      default:
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
      } else if(isWideningReferenceConversion(typeRightSide.tokenType, typeRightSide.getAbsolutePath(), typeRightSide.isArray,
                                              typeLeftSide.tokenType, typeLeftSide.getAbsolutePath(), typeLeftSide.isArray)) {
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
      token.children.get(0).getTokenType() == TokenType.CastExpression) return;

    if(token.children.get(0).getTokenType() == TokenType.Name) {
      Name name = (Name) token.children.get(0);
      Declaration determinedDecl = determineDeclaration(name, new Class[] {FormalParameter.class,
                                                                           FieldDeclaration.class,
                                                                           LocalVariableDeclaration.class});
      tokenStack.push(new TypeCheckToken(determinedDecl));
    } else if(token.children.size() == 2) {
      // No need to pop since if the type is valid we would've to push it back on the stack anyways
      TokenType type = tokenStack.peek().tokenType;

      assertNotArray(token, tokenStack.peek(), OperandSide.RIGHT, token.children.get(0).getLexeme());
      if (type != TokenType.BOOLEAN) {
        throw new VisitorException("Unary operator '! UnaryExpression' was expecting UnaryExpression to be boolean but found " + type, token);
      }
    }
  }

  @Override
  public void visit(ConstructorDeclarator token) throws VisitorException {
    super.visit(token);
    if (!token.getIdentifier().getLexeme().equals(node.identifier)) {
      throw new VisitorException("Constructor name " + token.getIdentifier() + " does not match class name " + node.identifier, token);
    }
  }

  @Override
  public void visit(Expression token) throws VisitorException {
  }

  @Override
  public void visit(ClassInstanceCreationExpression token) throws VisitorException {
    super.visit(token);

    List<TypeCheckToken> arguments = new ArrayList<TypeCheckToken>();
    if(token.argumentList != null) {
      for (int i = 0; i < token.argumentList.numArguments(); i++) {
        arguments.add(tokenStack.pop());
      }
    }

    Name name = token.getClassType();
    if (hierarchyGraph.get(name.getAbsolutePath()).isAbstract()) {
      // ClassType was abstract
      throw new VisitorException("Abstract class " + " cannot be instantiated", token);
    }

    String constructor = name.getAbsolutePath() + "." + name.getLexeme();
    List<Token> matchingDeclarations = symbolTable.findWithPrefixOfAnyType(constructor, new Class [] {ConstructorDeclaration.class});
    Declaration constructorDeclaration = matchCall(matchingDeclarations, false, arguments, name);

    Declaration classDecl = determineDeclaration(name, new Class [] {ClassDeclaration.class});
    tokenStack.push(new TypeCheckToken(classDecl));
  }

  @Override
  public void visit(LocalVariableDeclaration decl) throws VisitorException {
    TypeCheckToken assignedType = tokenStack.pop();

    TokenType declType = decl.type.isPrimitiveType() ? decl.type.getType().getTokenType() : TokenType.OBJECT;
    String declAbsolutePath = !decl.type.isReferenceType() ? null : decl.type.getReferenceName().getAbsolutePath();
    boolean declIsArray = decl.type.isArray();

    TokenType [] validTypes = {TokenType.BOOLEAN, TokenType.INT, TokenType.CHAR, TokenType.BYTE, TokenType.SHORT};

    try {
      if (declIsArray == assignedType.isArray && validType(declType, validTypes) && declType == assignedType.tokenType) {
      } else if(declIsArray == false && assignedType.isArray == false &&
              decl.type.isPrimitiveType() && assignedType.isPrimitiveType() &&
              isWideningPrimitiveConversion(assignedType.tokenType, declType)) {
      } else if (isWideningReferenceConversion(assignedType.tokenType, assignedType.getAbsolutePath(), assignedType.isArray,
                                               declType, declAbsolutePath, declIsArray)) {
      } else {
        throw new VisitorException("Assignment should be of same type or from parent to subclass.  Found: " + assignedType.toString() + " being assigned to "  + declType.toString(), decl);
      }
    } catch (TypeHierarchyException e) {
      throw new VisitorException(e.getMessage(), decl);
    }
  }

  @Override
  public void visit(ArrayAccess token) throws VisitorException {
    TypeCheckToken expressionType = tokenStack.pop();
    if (expressionType.tokenType != TokenType.INT) {
      throw new VisitorException("ArrayAccess requires an integer but found " + expressionType.tokenType.toString(), token);
    }

    if (token.name != null) {
      Name name = token.name;

      Declaration determinedDecalaration = determineDeclaration(name, new Class[] {FormalParameter.class,
                                                                                   FieldDeclaration.class,
                                                                                   LocalVariableDeclaration.class});
      tokenStack.push(new TypeCheckToken(determinedDecalaration));
    } else {
      // Primary case: No need to do anything as Primary type is on top of the stack.
      // We check to make sure Expression is of type int at the top
    }
  }

  @Override
  public void visit(ArrayCreationExpression token) throws VisitorException {
    TypeCheckToken expressionType = tokenStack.pop();
    if (expressionType.tokenType != TokenType.INT) {
      throw new VisitorException("ArrayAccess requires an integer but found " + expressionType.tokenType.toString(), token);
    }

    if(token.isPrimitiveType()) {
      tokenStack.push(new TypeCheckToken(token.primitiveType.getType().getTokenType(), true));
    } else {
      Declaration determined = determineDeclaration(token.name, new Class[] {ClassDeclaration.class});
      tokenStack.push(new TypeCheckToken(determined, true));
    }

    //? Cant it be initiated
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

    String absoluteFieldAccess = firstIdentifier.getAbsolutePath() + '.' + token.identifier.getLexeme();
    List<Token> potentialFields = symbolTable.findWithPrefixOfAnyType(absoluteFieldAccess, new Class[] {FieldDeclaration.class});
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

      Declaration determinedDecl = determineDeclaration(name, new Class[] {LocalVariableDeclaration.class,
                                                                           FieldDeclaration.class,
                                                                           FormalParameter.class});
      tokenStack.push(new TypeCheckToken(determinedDecl));
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
        tokenStack.push(new TypeCheckToken(token.primitiveType.getType().getTokenType(), token.isArrayCast()));
      } else if (tokenToCast.isPrimitiveType() && token.isPrimitiveType() &&
              (isWideningPrimitiveConversion(tokenToCast.tokenType, token.primitiveType.getType().getTokenType()) ||
               isNarrowingPrimitiveConversion(tokenToCast.tokenType, token.primitiveType.getType().getTokenType()))) {
        tokenStack.push(new TypeCheckToken(token.primitiveType.getType().getTokenType(), token.isArrayCast()));
        //TODO: Might need to change name.getAbsolutePath to name.getDeclaration and determine the declaration and use path from that
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
    if(token.argumentList != null) {
      for (int i = 0; i < token.argumentList.numArguments(); i++) {
        arguments.add(tokenStack.pop());
      }
    }

    List<Token> matchingDeclarations;
    if(token.isOnPrimary()) {
      TypeCheckToken primary = tokenStack.pop();
      String methodToCall = primary.getAbsolutePath() + "." + token.identifier.getLexeme();
      matchingDeclarations = symbolTable.findWithPrefixOfAnyType(methodToCall, new Class [] {MethodDeclaration.class});
    } else {
      Name name = (Name) token.name;
      matchingDeclarations = getAllMatchinDeclarations(name, new Class [] {MethodDeclaration.class});
    }

    Declaration methodDeclaration = matchCall(matchingDeclarations, true, arguments, token.name);
    if(methodDeclaration.type == null) {
      tokenStack.push(new TypeCheckToken(TokenType.VOID, false));
    } else if(methodDeclaration.type.isPrimitiveType()) {
      tokenStack.push(new TypeCheckToken(methodDeclaration.type.getType().getTokenType(), methodDeclaration.type.isArray()));
    } else {
      Declaration determinedDecl = determineDeclaration(methodDeclaration.type.getReferenceName(), new Class[] {ClassDeclaration.class});
      tokenStack.push(new TypeCheckToken(determinedDecl, methodDeclaration.type.isArray()));
    }
  }

  @Override
  public void visit(StatementExpression token) throws VisitorException {
    super.visit(token);
    if(tokenStack.size() == 0) {
      throw new VisitorException("Expected an value on stack, but found none", token);
    }

    tokenStack.pop();
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

  private boolean isWideningReferenceConversion(TokenType fromType, String fromAbsolutePath, boolean fromIsArray,
                                                TokenType toType, String toAbsolutePath, boolean toIsArray) throws TypeHierarchyException {
      if (toIsArray == fromIsArray && toType == TokenType.OBJECT && fromType == TokenType.OBJECT &&
              hierarchyGraph.areNodesConnectedOneWay(fromAbsolutePath, toAbsolutePath)) {
        return true;
      } else if (toType == TokenType.OBJECT && fromType == TokenType.NULL) {
        return true;
      } else if(toIsArray && fromType == TokenType.NULL) {
        return true;
      } else if(toType == TokenType.OBJECT && fromIsArray && toAbsolutePath.equals(SERIALIZABLE_CLASS_PATH)) {
        return true;
      } else if(toType == TokenType.OBJECT && fromIsArray && toAbsolutePath.equals(OBJECT_CLASS_PATH)) {
        return true;
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

  private Declaration determineDeclaration(Name name, Class [] classes) throws VisitorException {
    Set<Class> classSet = new HashSet<Class>(Arrays.asList(classes));
    if(name.getDeclarationTypes() == null) {
      throw new VisitorException("Found no declarations for " + name.getLexeme(), name);
    }

    for(Declaration declaration : name.getDeclarationTypes()) {
      if(classSet.contains(declaration.getClass())) {
        return declaration;
      }
    }

    throw new VisitorException("Can not determine declaration " + name.getLexeme(), name);
  }

  private List<Token> getAllMatchinDeclarations(Name name, Class [] classes) throws VisitorException {
    Set<Class> classSet = new HashSet<Class>(Arrays.asList(classes));

    ArrayList<Token> declarations = new ArrayList<Token>();
    for(Declaration declaration : name.getDeclarationTypes()) {
      if(classSet.contains(declaration.getClass())) {
        declarations.add(declaration);
      }
    }

    if(declarations.size() == 0) {
      throw new VisitorException("Can not determine declaration " + name.getLexeme(), name);
    }

    return declarations;
  }

  private Declaration matchCall(List<Token> matchingDeclarations, boolean isMethod, List<TypeCheckToken> argumentsToMethod, Token context) throws VisitorException {
    for (Token declaration : matchingDeclarations) {
      FormalParameterList parameterList =
              isMethod ? ((MethodDeclaration) declaration).methodHeader.paramList
                  : ((ConstructorDeclaration) declaration).declarator.getParameterList();

      if(parameterList == null) {
        if(argumentsToMethod.size() == 0) {
          return (Declaration) declaration;
        } else {
          continue;
        }
      }

      List<FormalParameter> parameters = parameterList.getFormalParameters();
      if(parameters.size() == argumentsToMethod.size()) {
        boolean allParametersMatch = true;
        for(int i = 0; i < parameters.size(); i++) {
          FormalParameter callParameter = parameters.get(i);
          TypeCheckToken argumentParameter = argumentsToMethod.get(parameters.size() - i - 1);
          if(callParameter.isPrimitive() && argumentParameter.isPrimitiveType()
                  && callParameter.isArray() == argumentParameter.isArray
                  && callParameter.getType().getTokenType() == argumentParameter.tokenType) {
          } else if(callParameter.isReferenceType() && argumentParameter.tokenType == TokenType.OBJECT
                  && callParameter.isArray() == argumentParameter.isArray &&
                  callParameter.type.getReferenceName().getAbsolutePath().equals(argumentParameter.getAbsolutePath())) {
          } else {
            allParametersMatch = false;
            break;
          }
        }

        if(allParametersMatch) {
          return (Declaration) declaration;
        }
      }
    }

    throw new VisitorException("Can not find any " + (isMethod ? "Method" : "Constructor") + " declaration for " + context.getLexeme(), context);
  }
}
