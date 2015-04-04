package visitor;

import exception.TypeCheckingVisitorException;
import exception.TypeHierarchyException;
import exception.VisitorException;
import symbol.SymbolTable;
import token.*;
import type.hierarchy.HierarchyGraph;
import type.hierarchy.HierarchyGraphNode;
import type.hierarchy.Method;

import java.util.*;

public class TypeCheckingVisitor extends BaseVisitor {
  private enum OperandSide {LEFT, RIGHT};
  private final String STRING_CLASS_PATH = "java.lang.String";
  private final String SERIALIZABLE_CLASS_PATH = "java.io.Serializable";
  private final String CLONEABLE_CLASS_PATH = "java.lang.Cloneable";
  private final String OBJECT_CLASS_PATH = "java.lang.Object";

  private final SymbolTable symbolTable;
  private final Map<CompilationUnit, HierarchyGraphNode> compilationUnitToNode;
  private final HierarchyGraph hierarchyGraph;
  public Stack<TypeCheckToken> tokenStack;
  public Stack<TypeCheckToken> returnCallStack;
  private HierarchyGraphNode node;
  private CompilationUnit unit;

  private boolean explicitThisUsedInContext;

  public TypeCheckingVisitor(SymbolTable symbolTable, HierarchyGraph hierarchyGraph, Map<CompilationUnit, HierarchyGraphNode> compilationUnitToNode) {
    this.symbolTable = symbolTable;
    this.compilationUnitToNode = compilationUnitToNode;
    this.hierarchyGraph = hierarchyGraph;
    explicitThisUsedInContext = false;
  }

  public void typeCheckUnits(List<CompilationUnit> units) throws VisitorException {
    for(CompilationUnit unit : units) {
      explicitThisUsedInContext = false;
      // we don't need to type check interfaces
      if (unit.typeDeclaration.classDeclaration == null) {
        continue;
      }

      tokenStack = new Stack<TypeCheckToken>();
      returnCallStack = new Stack<TypeCheckToken>();
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
      case BooleanLiteral:
        literalToken = new TypeCheckToken(TokenType.BOOLEAN);
        break;
      case CHAR_LITERAL:
        literalToken = new TypeCheckToken(TokenType.CHAR);
        break;
      case NULL:
        literalToken = new TypeCheckToken(TokenType.NULL);
        break;
      default:
        throw new TypeCheckingVisitorException("Unexpected literal: " + token.getLexeme() + " of type " + token.getTokenType(), token);
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
      throw new TypeCheckingVisitorException("Boolean OR expression expected boolean || boolean but found " + type1.tokenType.toString() + " || " + type2.tokenType.toString(), token);
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
      throw new TypeCheckingVisitorException("Boolean AND expression expected boolean && boolean but found " + type1.tokenType.toString() + " && " + type2.tokenType.toString(), token);
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
      throw new TypeCheckingVisitorException("Boolean OR expression expected boolean | boolean but found " + type1.tokenType.toString() + " | " + type2.tokenType.toString(), token);
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
      throw new TypeCheckingVisitorException("Boolean OR expression expected boolean & boolean but found " + type1.toString() + " & " + type2.toString(), token);
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
              (leftSide.getAbsolutePath().equals(rightSide.getAbsolutePath()) || hierarchyGraph.areNodesConnected(rightSide.getAbsolutePath(), leftSide.getAbsolutePath()))) {
        tokenStack.push(new TypeCheckToken(TokenType.BOOLEAN));
      } else if (leftSide.tokenType == TokenType.OBJECT && rightSide.tokenType == TokenType.NULL) {
        tokenStack.push(new TypeCheckToken(TokenType.BOOLEAN));
      } else {
        throw new TypeCheckingVisitorException("Boolean Equality expression expected both of same inherited type but found " + rightSide.toString() + " & " + leftSide.toString(), token);
      }
    } catch(TypeHierarchyException e) {
      throw new TypeCheckingVisitorException(e.getMessage(), token);
    }
  }

  @Override
  public void visit(RelationalExpression token) throws VisitorException {
    super.visit(token);
    if (token.children.size() == 1) return;

    if (token.children.get(1).getTokenType() == TokenType.INSTANCEOF) {
      ReferenceType reference = (ReferenceType) token.children.get(2);
      TypeCheckToken typeLeftSide = tokenStack.pop();

      TokenType referenceType = reference.isPrimitiveType() ? reference.getType().getTokenType() : TokenType.OBJECT;
      String referenceAbsolutePath = !reference.isReferenceType() ? null : reference.getReferenceName().getAbsolutePath();
      boolean referenceIsArray = reference.isArray();

      if((!typeLeftSide.isArray && typeLeftSide.tokenType != TokenType.OBJECT && typeLeftSide.tokenType != TokenType.NULL)) {
        throw new TypeCheckingVisitorException("InstanceOf expression expected Array|Object instanceOf Array|Object but found " +
                typeLeftSide + " instanceOf " + referenceType + " " + referenceAbsolutePath + " " + referenceIsArray , token);
      }

      TokenType [] validTypes = {TokenType.BOOLEAN, TokenType.INT, TokenType.CHAR, TokenType.BYTE, TokenType.SHORT};

      try {
        if (typeLeftSide.isArray && referenceIsArray && validType(typeLeftSide.tokenType, validTypes) && typeLeftSide.tokenType == referenceType) {
          tokenStack.push(new TypeCheckToken(TokenType.BOOLEAN));
        } else if(isWideningReferenceConversion(referenceType, referenceAbsolutePath, referenceIsArray,
                typeLeftSide.tokenType, typeLeftSide.getAbsolutePath(), typeLeftSide.isArray)) {
          tokenStack.push(new TypeCheckToken(TokenType.BOOLEAN));
        } else if(isWideningReferenceConversion(typeLeftSide.tokenType, typeLeftSide.getAbsolutePath(), typeLeftSide.isArray,
                referenceType, referenceAbsolutePath, referenceIsArray)) {
          tokenStack.push(new TypeCheckToken(TokenType.BOOLEAN));
        } else {
          throw new TypeCheckingVisitorException("InstanceOf expression expected Array|Object instanceOf Array|Object of subtypes but found of " +
                  typeLeftSide + " instanceOf " + referenceType + " " + referenceAbsolutePath + " " + referenceIsArray , token);
        }
      } catch (TypeHierarchyException e) {
        throw new TypeCheckingVisitorException(e.getMessage(), token);
      }
    } else {
      TypeCheckToken rightType = tokenStack.pop();
      TypeCheckToken leftType = tokenStack.pop();
      TokenType[] validTypes = {TokenType.INT, TokenType.CHAR, TokenType.BYTE, TokenType.SHORT};

      if (!validTypes(rightType.tokenType, leftType.tokenType, validTypes)) {
        throw new TypeCheckingVisitorException("Relational expression expected 'int|char|byte|short RelationalOperator int|char|byte|short' but found " + rightType + " RelationalOperator " + leftType, token);
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

    // These types could be used with the '+' and '-' operators and the result is an int
    TokenType[]  validMinusPlusTypes = {TokenType.SHORT, TokenType.INT, TokenType.BYTE, TokenType.CHAR};

    if (token.children.get(1).getTokenType() == TokenType.MINUS_OP) {
      assertNotArray(token, rightSide, OperandSide.RIGHT, token.children.get(1).getLexeme());
      assertNotArray(token, leftSide, OperandSide.LEFT, token.children.get(1).getLexeme());

      if (validTypes(leftSide.tokenType, rightSide.tokenType, validMinusPlusTypes)) {
        tokenStack.push(new TypeCheckToken(TokenType.INT));
      } else {
        throw new TypeCheckingVisitorException("AdditiveExpression expected 'short|int|byte|char - short|int|byte|char but found " + leftSide + " - " + rightSide, token);
      }
    } else if (token.children.get(1).getTokenType() == TokenType.PLUS_OP) {
      TokenType[] validStringConcatTypes = {TokenType.SHORT, TokenType.INT, TokenType.BYTE, TokenType.CHAR, TokenType.NULL, TokenType.BOOLEAN, TokenType.OBJECT};
      if (!leftSide.isArray && leftSide.tokenType == TokenType.OBJECT && leftSide.getAbsolutePath().equals(STRING_CLASS_PATH) && validType(rightSide.tokenType, validStringConcatTypes) ||
        !rightSide.isArray && rightSide.tokenType == TokenType.OBJECT && rightSide.getAbsolutePath().equals(STRING_CLASS_PATH) && validType(leftSide.tokenType, validStringConcatTypes)) {
        tokenStack.push(createStringToken());
      } else if (validTypes(leftSide.tokenType, rightSide.tokenType, validMinusPlusTypes)) {
        assertNotArray(token, rightSide, OperandSide.RIGHT, token.children.get(1).getLexeme());
        assertNotArray(token, leftSide, OperandSide.LEFT, token.children.get(1).getLexeme());

        tokenStack.push(new TypeCheckToken(TokenType.INT));
      } else {
        throw new TypeCheckingVisitorException("String concatenation found invalid types " + leftSide + " + " + rightSide, token);
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
      throw new TypeCheckingVisitorException("Expected short|int|byte|char " + token.children.get(1).getLexeme() +
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
      throw new TypeCheckingVisitorException("Unary operator '- UnaryExpression' was expecting UnaryExpression to be of type short|int|byte|char but found " + type, token);
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
        throw new TypeCheckingVisitorException("Assignment should be of same type or from parent to subclass.  Found: " + typeRightSide.toString() + " being assigned to "  + typeLeftSide.toString(), token);
      }
    } catch (TypeHierarchyException e) {
      throw new TypeCheckingVisitorException(e.getMessage(), token);
    }
  }

  @Override
  public void visit(UnaryExpressionNotMinus token) throws VisitorException {
    super.visit(token);
    if (token.children.get(0).getTokenType() == TokenType.Primary ||
      token.children.get(0).getTokenType() == TokenType.CastExpression) return;

    if(token.children.get(0).getTokenType() == TokenType.Name) {
      Name name = (Name) token.children.get(0);
      if(name.isUsedInCast()) {
        return;
      }

      // Only determine declarations on fields and not Classes which only occur in casts
      // The latter scenario will be handled in the cast
      Declaration determinedDecl = determineDeclaration(name, new Class[] {FormalParameter.class,
                                                                           FieldDeclaration.class,
                                                                           LocalVariableDeclaration.class});



      if (determinedDecl instanceof FieldDeclaration) {
        if (name != null && name.classifiedType == Name.ClassifiedType.Type &&
          determinedDecl instanceof FieldDeclaration &&
          !((FieldDeclaration) determinedDecl).modifiers.isStatic()) {
          throw new TypeCheckingVisitorException("Non static field " + ((FieldDeclaration) determinedDecl).identifier.getLexeme() +
            " of class " + determinedDecl.identifier.getLexeme() + " is used as static", token);
        } else if (name != null && name.classifiedType == Name.ClassifiedType.NonStaticExpr &&
          ((FieldDeclaration) determinedDecl).modifiers.isStatic()) {
          throw new TypeCheckingVisitorException("static filed " + ((FieldDeclaration) determinedDecl).identifier.getLexeme() +
            " of class " + determinedDecl.identifier.getLexeme() + " is used as non static", token);
        }

        Declaration clazz = symbolTable.getClass(determinedDecl);

        if (clazz.getAbsolutePath().equals(node.getFullname()) &&
          !((FieldDeclaration) determinedDecl).modifiers.isStatic() &&
          !(token.children.get(0) instanceof Primary) &&
          token.children.get(0) instanceof Name && ((Name)token.children.get(0)).qualifiedName == null) {
          explicitThisUsedInContext = true;
        }
        if (name != null && name.getDeclarationPath() != null && name.getDeclarationPath().get(name.getDeclarationPath().size() - 1).type != null) {
          HierarchyGraphNode parent = hierarchyGraph.get(name.getDeclarationPath().get(name.getDeclarationPath().size()-1).type.getType().getLexeme());
          // Case where an instance is not used (e.g. static protected fields, protected fields, ...)
          if (parent != null && !parent.getFullname().equals(node.getFullname()) &&
            ((FieldDeclaration) determinedDecl).modifiers.isProtected() &&
            !parent.getPackageName().equals(node.getPackageName()) &&
            !hierarchyGraph.nodeAIsParentOfNodeB(node, parent)) {
            throw new TypeCheckingVisitorException("Protected field " + determinedDecl.identifier.getLexeme() +
              " is accessed outside of hierarchy and package", token);
          }
        }
        if (name != null) {
          // Case where an instance is used (e.g.  A a = new A();  a.protected_field
          HierarchyGraphNode parent = hierarchyGraph.get(clazz.getAbsolutePath());
          if (parent != null && !parent.getFullname().equals(node.getFullname()) &&
            (!hierarchyGraph.nodeAIsParentOfNodeB(parent, node) &&
              !parent.getPackageName().equals(node.getPackageName())) &&
            ((FieldDeclaration) determinedDecl).modifiers.isProtected()) {
            throw new TypeCheckingVisitorException("Protected field " +
              determinedDecl.getLexeme() +
              "accessed from outside of package or class hierarchy. Violating class: " +
              node.getFullname(), token);
          }
        }
      }

      String determinedAbsolutePath = determinedDecl.getAbsolutePath();
      String originalPath = name.getLexeme();
      String [] determinedAbsolutePathArr = determinedAbsolutePath.split("\\.");
      String [] originalPathArr = originalPath.split("\\.");

      if(determinedAbsolutePathArr.length == 0 || originalPathArr.length == 0) {
        throw new TypeCheckingVisitorException("variable needs to be defined: " + determinedAbsolutePath + " but had " + originalPath, token);
      }

      if(!determinedAbsolutePathArr[determinedAbsolutePathArr.length - 1].equals(originalPathArr[originalPathArr.length - 1])) {
        if(originalPathArr.length < 2) {
          throw new TypeCheckingVisitorException("Accessing undefined variable found: " + determinedAbsolutePath + " but had " + originalPath, token);
        }

        if(determinedDecl.type.isArray() && originalPathArr[originalPathArr.length - 1].equals("length") &&
                originalPathArr[originalPathArr.length - 2].equals(determinedAbsolutePathArr[determinedAbsolutePathArr.length - 1])) {
          tokenStack.push(new TypeCheckToken(TokenType.INT));
        } else  {
          throw new TypeCheckingVisitorException("Accessing undefined variable found: " + determinedAbsolutePath + " but had " + originalPath, token);
        }
      } else {
        tokenStack.push(new TypeCheckToken(determinedDecl));
      }
    } else if(token.children.size() == 2) {
      // No need to pop since if the type is valid we would've to push it back on the stack anyways
      TokenType type = tokenStack.peek().tokenType;

      assertNotArray(token, tokenStack.peek(), OperandSide.RIGHT, token.children.get(0).getLexeme());
      if (type != TokenType.BOOLEAN) {
        throw new TypeCheckingVisitorException("Unary operator '! UnaryExpression' was expecting UnaryExpression to be boolean but found " + type, token);
      }
    }
  }

  @Override
  public void visit(ConstructorDeclaration token) throws VisitorException {
    super.visit(token);
    while(!returnCallStack.isEmpty()) {
      TypeCheckToken returnToken = returnCallStack.pop();
      if(returnToken.tokenType != TokenType.VOID) {
        throw new TypeCheckingVisitorException("Can not return not void types in constructor: found " + returnToken.tokenType, token);
      }
    }
  }

  @Override
  public void visit(MethodDeclaration token) throws VisitorException {
    super.visit(token);
    if (token.methodHeader.modifiers.isStatic() && explicitThisUsedInContext) {
      throw new TypeCheckingVisitorException("explicit this used in static context. Class: " + node.getFullname() + " Method: " + token.methodHeader.identifier.getLexeme(), token);
    }
    while(!returnCallStack.isEmpty()) {
      TypeCheckToken returnToken = returnCallStack.pop();
      if(token.methodHeader.voidType != null) {
        if (returnToken.tokenType != TokenType.VOID) {
          throw new TypeCheckingVisitorException("Can not return not void types in constructor: found " + returnToken.tokenType, token);
        }
      } else {
        TokenType expectedType = token.methodHeader.type.isPrimitiveType() ? token.methodHeader.type.getType().getTokenType() : TokenType.OBJECT;
        String expectedAbsolutePath = !token.methodHeader.type.isReferenceType() ? null : token.methodHeader.type.getReferenceName().getAbsolutePath();
        boolean expectedIsArray = token.methodHeader.type.isArray();

        TokenType [] validTypes = {TokenType.BOOLEAN, TokenType.INT, TokenType.CHAR, TokenType.BYTE, TokenType.SHORT};

        try {
          if (expectedIsArray == returnToken.isArray && validType(expectedType, validTypes) && expectedType == returnToken.tokenType) {
          } else if(expectedIsArray == false && returnToken.isArray == false &&
                  token.methodHeader.type.isPrimitiveType() && returnToken.isPrimitiveType() &&
                  isWideningPrimitiveConversion(returnToken.tokenType, expectedType)) {
          } else if (isWideningReferenceConversion(returnToken.tokenType, returnToken.getAbsolutePath(), returnToken.isArray,
                  expectedType, expectedAbsolutePath, expectedIsArray)) {
          } else {
            throw new TypeCheckingVisitorException("Return should be of same type or from parent to subclass.  Found: " + returnToken.toString() + " when should be "  + expectedType.toString() + " " + expectedAbsolutePath, token);
          }
        } catch (TypeHierarchyException e) {
          throw new TypeCheckingVisitorException(e.getMessage(), token);
        }
      }
    }
    // reset the this used in method body check, as we will be visiting another method.
    explicitThisUsedInContext = false;
  }

  @Override
  public void visit(ConstructorDeclarator token) throws VisitorException {
    super.visit(token);
    if (!token.getIdentifier().getLexeme().equals(node.identifier)) {
      throw new TypeCheckingVisitorException("Constructor name " + token.getIdentifier() + " does not match class name " + node.identifier, token);
    }

    if(node.extendsList != null && !node.extendsList.isEmpty()) {
      // Should only be 1
      HierarchyGraphNode parent = node.extendsList.get(0);
      if(!parent.getFullname().equals(OBJECT_CLASS_PATH)) {
        boolean hasDefaultConstructor = false;

        for(Method constructor : parent.constructors) {
          if(constructor.parameterTypes == null || constructor.parameterTypes.isEmpty()) {
            hasDefaultConstructor = true;
            break;
          }
        }

        if(!hasDefaultConstructor) {
          throw new TypeCheckingVisitorException("Parent " + parent.getFullname() + " requires default constructor for implicit super call from " + node.identifier, token);
        }
      }
    }
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
      throw new TypeCheckingVisitorException("Abstract class " + " cannot be instantiated", token);
    }

    String [] nameParts = name.getLexeme().split("\\.");
    String constructor = name.getAbsolutePath() + "." + nameParts[nameParts.length - 1];
    List<Token> matchingDeclarations = symbolTable.findWithPrefixOfAnyType(constructor, new Class [] {ConstructorDeclaration.class});
    Declaration constructorDeclaration = matchCall(matchingDeclarations, false, arguments, name);

    Declaration classDecl = determineDeclaration(name, new Class[]{ClassDeclaration.class});

    HierarchyGraphNode parent = hierarchyGraph.get(classDecl.getAbsolutePath());
    if (!parent.getFullname().equals(node.getFullname()) &&
      (!hierarchyGraph.nodeAIsParentOfNodeB(parent, node) ||
      !parent.getPackageName().equals(node.getPackageName())) &&
      ((ConstructorDeclaration)constructorDeclaration).modifiers.isProtected()) {
      throw new TypeCheckingVisitorException("Protected constructor " +
        constructor +
        "accessed from outside of package or class hierarchy. Violating class: " +
        node.getFullname(), token);
    }

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
        throw new TypeCheckingVisitorException("Assignment should be of same type or from parent to subclass.  Found: " + assignedType.toString() + " being assigned to "  + declType.toString() + " " + declAbsolutePath, decl);
      }
    } catch (TypeHierarchyException e) {
      throw new TypeCheckingVisitorException(e.getMessage(), decl);
    }
  }

  @Override
  public void visit(FieldDeclaration decl) throws VisitorException {
    // If variable isn't initialized, no need to check
    if(decl.expr == null) {
      return;
    }

    if (decl.modifiers.isStatic() && explicitThisUsedInContext) {
      throw new TypeCheckingVisitorException("explicit this used in static context. Class: " + node.getFullname() + " FieldDeclaration: " + decl.identifier.getLexeme(), decl);
    } else {
      explicitThisUsedInContext = false;
    }

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
        throw new TypeCheckingVisitorException("Assignment should be of same type or from parent to subclass.  Found: " + assignedType.toString() + " being assigned to "  + declType.toString() + " " + declAbsolutePath, decl);
      }
    } catch (TypeHierarchyException e) {
      throw new TypeCheckingVisitorException(e.getMessage(), decl);
    }
  }

  @Override
  public void visit(ArrayAccess token) throws VisitorException {
    TypeCheckToken expressionType = tokenStack.pop();

    TokenType [] validTypes = {TokenType.INT, TokenType.CHAR, TokenType.BYTE, TokenType.SHORT};
    if (!validType(expressionType.tokenType, validTypes)) {
      throw new TypeCheckingVisitorException("ArrayAccess requires an integer but found " + expressionType.tokenType.toString(), token);
    }

    if (token.name != null) {
      Name name = token.name;

      Declaration determinedDecalaration = determineDeclaration(name, new Class[] {FormalParameter.class,
                                                                                   FieldDeclaration.class,
                                                                                   LocalVariableDeclaration.class});
      if(!determinedDecalaration.type.isArray()) {
        throw new TypeCheckingVisitorException("Trying to dereference an array with an index: name=" + name.getLexeme(), token);
      }

      tokenStack.push(new TypeCheckToken(determinedDecalaration, false));
    } else {
      TypeCheckToken primaryAccess = tokenStack.pop();

      if(!primaryAccess.isArray) {
        throw new TypeCheckingVisitorException("Trying to dereference an array with an index: name=" + primaryAccess.tokenType, token);
      }

      primaryAccess.isArray = false;
      tokenStack.push(primaryAccess);
    }
  }

  @Override
  public void visit(ArrayCreationExpression token) throws VisitorException {
    TypeCheckToken expressionType = tokenStack.pop();

    TokenType [] validTypes = {TokenType.INT, TokenType.CHAR, TokenType.BYTE, TokenType.SHORT};
    if (!validType(expressionType.tokenType, validTypes)) {
      throw new TypeCheckingVisitorException("ArrayAccess requires an integer but found " + expressionType.tokenType.toString(), token);
    }

    if(token.isPrimitiveType()) {
      tokenStack.push(new TypeCheckToken(token.primitiveType.getType().getTokenType(), true));
    } else {
      Declaration determined = determineDeclaration(token.name, new Class[] {ClassDeclaration.class});
      tokenStack.push(new TypeCheckToken(determined, true));
    }
  }

  @Override
  public void visit(FieldAccess token) throws VisitorException {
    super.visit(token);
    TypeCheckToken firstIdentifier = tokenStack.pop();
    if (firstIdentifier.isArray) {
      if (token.identifier.getLexeme().equals("length")) {
        tokenStack.push(new TypeCheckToken(TokenType.INT));
        return;
      } else {
        throw new TypeCheckingVisitorException("Invalid array field access for field: " + token.identifier.getLexeme(), token);
      }
    }

    if(firstIdentifier.tokenType != TokenType.OBJECT) {
      throw new TypeCheckingVisitorException("Expected object when calling field " + token.identifier.getLexeme() + " but found " + firstIdentifier.tokenType, token);
    }

    List<Token> potentialFields = new ArrayList<Token>();
    List<FieldDeclaration> allFields = hierarchyGraph.get(firstIdentifier.getAbsolutePath()).getAllFields();
    for (Iterator<FieldDeclaration> it = allFields.listIterator(); it.hasNext(); ) {
      FieldDeclaration field = it.next();
      if(field.identifier.getLexeme().equals(token.identifier.getLexeme())) {
        potentialFields.add(field);
      }
    }

    if (potentialFields.isEmpty()) {
      throw new TypeCheckingVisitorException("No field could be resolved for field: " + token.identifier.getLexeme(), token);
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

      if (determinedDecl instanceof FieldDeclaration) {
        Declaration clazz = symbolTable.getClass(determinedDecl);

        if (clazz.getAbsolutePath().equals(node.getFullname())) {
          //explicitThisUsedInContext = true;
        }

        if (name != null && name.getDeclarationPath() != null && name.getDeclarationPath().get(name.getDeclarationPath().size() - 1).type != null) {
          HierarchyGraphNode parent = hierarchyGraph.get(name.getDeclarationPath().get(name.getDeclarationPath().size() - 1).type.getType().getLexeme());
          // Case where an instance is not used (e.g. static protected fields, protected fields, ...)
          if (parent != null && !parent.getFullname().equals(node.getFullname()) &&
            ((FieldDeclaration) determinedDecl).modifiers.isProtected() &&
            !parent.getPackageName().equals(node.getPackageName()) &&
            !hierarchyGraph.nodeAIsParentOfNodeB(node, parent) &&
            ((Name)token.children.get(0)).qualifiedName != null) {
            throw new TypeCheckingVisitorException("Protected field " + determinedDecl.identifier.getLexeme() +
              " is accessed outside of hierarchy and package", token);
          }
        }
        if (name != null) {
          // Case where an instance is used (e.g.  A a = new A();  a.protected_field
          HierarchyGraphNode parent = hierarchyGraph.get(clazz.getAbsolutePath());
          if (parent != null && !parent.getFullname().equals(node.getFullname()) &&
            (!hierarchyGraph.nodeAIsParentOfNodeB(parent, node) &&
              !parent.getPackageName().equals(node.getPackageName())) &&
            ((FieldDeclaration) determinedDecl).modifiers.isProtected()) {
            throw new TypeCheckingVisitorException("Protected field " +
              determinedDecl.getLexeme() +
              "accessed from outside of package or class hierarchy. Violating class: " +
              node.getFullname(), token);
          }
        }
      }
      tokenStack.push(new TypeCheckToken(determinedDecl));
    }
  }

  @Override
  public void visit(CastExpression token) throws VisitorException {
    super.visit(token);

    TypeCheckToken tokenToCast = tokenStack.pop();

    TypeCheckToken cast = null;
    if(token.isName()) {
      Declaration determinedNameDecl = determineDeclaration(token.name, new Class[]{ClassDeclaration.class,
                                                                                    InterfaceDeclaration.class});
      cast = new TypeCheckToken(determinedNameDecl, token.isArrayCast());
    }

    try {
      if (tokenToCast.isArray == token.isArrayCast() && tokenToCast.isPrimitiveType() && token.isPrimitiveType() && tokenToCast.tokenType == token.primitiveType.getType().getTokenType()) {
        tokenStack.push(new TypeCheckToken(token.primitiveType.getType().getTokenType(), token.isArrayCast()));
      } else if (!tokenToCast.isArray && !token.isArrayCast() && tokenToCast.isPrimitiveType() && token.isPrimitiveType() &&
              (isWideningPrimitiveConversion(tokenToCast.tokenType, token.primitiveType.getType().getTokenType()) ||
                      isNarrowingPrimitiveConversion(tokenToCast.tokenType, token.primitiveType.getType().getTokenType()))) {
        tokenStack.push(new TypeCheckToken(token.primitiveType.getType().getTokenType(), token.isArrayCast()));
      } else if(cast != null && isWideningReferenceConversion(cast.tokenType, cast.getAbsolutePath(), cast.isArray,
              tokenToCast.tokenType, tokenToCast.getAbsolutePath(), tokenToCast.isArray)) {
        tokenStack.push(cast);
      } else if(cast != null && isWideningReferenceConversion(tokenToCast.tokenType, tokenToCast.getAbsolutePath(), tokenToCast.isArray,
              cast.tokenType, cast.getAbsolutePath(), cast.isArray)) {
        tokenStack.push(cast);
      } else {
        throw new TypeCheckingVisitorException("Not castable types: "  + token.toString() + " and " + tokenToCast.toString(), token);
      }
    } catch (TypeHierarchyException e) {
      throw new TypeCheckingVisitorException(e.getMessage(), token);
    }
  }

  @Override
  public void visit(Primary token) throws VisitorException {
    super.visit(token);

    if (token.children.get(0).getTokenType() == TokenType.THIS) {
      explicitThisUsedInContext = true;
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
      if(primary.tokenType != TokenType.OBJECT) {
        throw new TypeCheckingVisitorException("Expected object when calling method " + token.identifier.getLexeme() + " but found " + primary.tokenType, token);
      }

      matchingDeclarations = new ArrayList<Token>();
      List<BaseMethodDeclaration> allMethods = hierarchyGraph.get(primary.getAbsolutePath()).getAllMethods();

      for (Iterator<BaseMethodDeclaration> it = allMethods.listIterator(); it.hasNext(); ) {
        BaseMethodDeclaration method = it.next();
        if(method instanceof MethodDeclaration && ((MethodDeclaration) method).methodHeader.identifier.getLexeme().equals(token.identifier.getLexeme())
                || method instanceof AbstractMethodDeclaration && ((AbstractMethodDeclaration) method).methodHeader.identifier.getLexeme().equals(token.identifier.getLexeme())) {
          matchingDeclarations.add(method);
        }
      }
    } else {
      Name name = (Name) token.name;
      matchingDeclarations = getAllMatchinDeclarations(name, new Class [] {MethodDeclaration.class});
    }

    Declaration methodDeclaration = matchCall(matchingDeclarations, true, arguments, token.name == null ? token.identifier : token.name);
      Declaration clazz = symbolTable.getClass(methodDeclaration);

      if (token.name != null && token.name.classifiedType == Name.ClassifiedType.Type &&
        !((MethodDeclaration) methodDeclaration).methodHeader.modifiers.isStatic()) {
        throw new TypeCheckingVisitorException("Non static method " + ((MethodDeclaration) methodDeclaration).methodHeader.identifier.getLexeme() +
          " of class " + clazz.identifier.getLexeme() + " is used as static", token);

      } else if (token.name != null && token.name.classifiedType == Name.ClassifiedType.NonStaticExpr &&
        ((MethodDeclaration) methodDeclaration).methodHeader.modifiers.isStatic()) {
        throw new TypeCheckingVisitorException("static method " + ((MethodDeclaration) methodDeclaration).methodHeader.identifier.getLexeme() +
          " of class " + clazz.identifier.getLexeme() + " is used as non static", token);
      } else if (token.name != null && token.name.simpleName != null && token.name.classifiedType == Name.ClassifiedType.Ambiguous &&
        ((MethodDeclaration) methodDeclaration).methodHeader.modifiers.isStatic()) {
        throw new TypeCheckingVisitorException("Calls a static method without naming the class. ", token);
      }

      if (clazz.getAbsolutePath().equals(node.getFullname()) && !((MethodDeclaration) methodDeclaration).methodHeader.modifiers.isStatic() && token.primary == null && token.name.qualifiedName == null) {
        explicitThisUsedInContext = true;
      }

    if (token.name != null && token.name.getDeclarationPath() != null && token.name.getDeclarationPath().get(token.name.getDeclarationPath().size() - 1).type != null) {
      HierarchyGraphNode parent = hierarchyGraph.get(token.name.getDeclarationPath().get(token.name.getDeclarationPath().size() - 1).type.getType().getLexeme());
      // Case where an instance is not used (e.g. static protected methods, protected methods, ...)
      if (parent != null && !parent.getFullname().equals(node.getFullname()) &&
        ((MethodDeclaration) methodDeclaration).methodHeader.modifiers.isProtected() &&
        !parent.getPackageName().equals(node.getPackageName()) &&
        !hierarchyGraph.nodeAIsParentOfNodeB(node, parent) &&
        ((Name)token.children.get(0)).qualifiedName != null) {
        throw new TypeCheckingVisitorException("Protected field " + methodDeclaration.identifier.getLexeme() +
          " is accessed outside of hierarchy and package", token);
      }
    }
    if (token.name != null) {
      HierarchyGraphNode parent = hierarchyGraph.get(clazz.getAbsolutePath());
      // Case where an instance is used (e.g.  A a = new A();  a.protected_method();
      if (parent != null && !parent.getFullname().equals(node.getFullname()) &&
        (!hierarchyGraph.nodeAIsParentOfNodeB(parent, node) &&
          !parent.getPackageName().equals(node.getPackageName())) &&
        ((MethodDeclaration) methodDeclaration).methodHeader.modifiers.isProtected()) {
        throw new TypeCheckingVisitorException("Protected method " +
          methodDeclaration.getLexeme() +
          "accessed from outside of package or class hierarchy. Violating class: " +
          node.getFullname(), token);
      }
    }

    if(methodDeclaration.type == null) {
      tokenStack.push(new TypeCheckToken(TokenType.VOID, false));
    } else if(methodDeclaration.type.isPrimitiveType()) {
      tokenStack.push(new TypeCheckToken(methodDeclaration.type.getType().getTokenType(), methodDeclaration.type.isArray()));
    } else {
      Declaration determinedDecl = determineDeclaration(methodDeclaration.type.getReferenceName(), new Class[] {ClassDeclaration.class,
                                                                                                                InterfaceDeclaration.class});
      tokenStack.push(new TypeCheckToken(determinedDecl, methodDeclaration.type.isArray()));
    }
  }

  @Override
  public void visit(StatementExpression token) throws VisitorException {
    super.visit(token);
    if(tokenStack.size() == 0) {
      throw new TypeCheckingVisitorException("Expected an value on stack, but found none", token);
    }

    tokenStack.pop();
  }

  @Override
  public void visit(ReturnStatement token) throws VisitorException {
    super.visit(token);
    if(token.children.get(1).getTokenType() == TokenType.Expression) {
      // Check may not be necessary, but just keep it in case
      if (tokenStack.size() == 0) {
        throw new TypeCheckingVisitorException("Expected an value on stack, but found none", token);
      }

      TypeCheckToken expression = tokenStack.pop();
      if(expression.tokenType == TokenType.VOID) {
        throw new TypeCheckingVisitorException("Can not return void values", token);
      }

      returnCallStack.push(expression);
    } else {
      // Push to indicate void return
      returnCallStack.push(new TypeCheckToken(TokenType.VOID));
    }
  }

  @Override
  public void visit(IfThenStatement token) throws VisitorException {
    super.visit(token);
    ensureBooleanCondition(token, "if");
  }

  @Override
  public void visit(IfThenElseStatement token) throws VisitorException {
    super.visit(token);
    ensureBooleanCondition(token, "if");
  }

  @Override
  public void visit(IfThenElseStatementNoShortIf token) throws VisitorException {
    super.visit(token);
    ensureBooleanCondition(token, "if");
  }

  @Override
  public void visit(WhileStatement token) throws VisitorException {
    super.visit(token);
    ensureBooleanCondition(token, "while");
  }

  @Override
  public void visit(WhileStatementNoShortIf token) throws VisitorException {
    super.visit(token);
    ensureBooleanCondition(token, "while");
  }

  @Override
  public void visit(ForStatement token) throws VisitorException {
    super.visit(token);
    if(token.expression != null) {
      ensureBooleanCondition(token, "for");
    }
  }

  @Override
  public void visit(ForStatementNoShortIf token) throws VisitorException {
    super.visit(token);
    if(token.expression != null) {
      ensureBooleanCondition(token, "for");
    }
  }

  private void ensureBooleanCondition(Token context, String contextStr) throws VisitorException{
    TypeCheckToken expression = tokenStack.pop();
    if (expression.tokenType != TokenType.BOOLEAN) {
      throw new TypeCheckingVisitorException("Expected boolean expression in " + contextStr + " but found " + expression, context);
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

  private boolean isWideningReferenceConversion(TokenType fromType, String fromAbsolutePath, boolean fromIsArray,
                                                TokenType toType, String toAbsolutePath, boolean toIsArray) throws TypeHierarchyException {
    if (toIsArray == fromIsArray && toType == TokenType.OBJECT && fromType == TokenType.OBJECT &&
          fromAbsolutePath.equals(toAbsolutePath)) {
      return true;
    } else if (toIsArray == fromIsArray && toType == TokenType.OBJECT && fromType == TokenType.OBJECT &&
            hierarchyGraph.areNodesConnectedOneWay(toAbsolutePath, fromAbsolutePath)) {
      return true;
    } else if (toType == TokenType.OBJECT && fromType == TokenType.NULL) {
      return true;
    } else if(toIsArray && fromType == TokenType.NULL) {
      return true;
    } else if(toType == TokenType.OBJECT && fromIsArray && toAbsolutePath.equals(SERIALIZABLE_CLASS_PATH)) {
      return true;
    } else if(toType == TokenType.OBJECT && fromIsArray && toAbsolutePath.equals(CLONEABLE_CLASS_PATH)) {
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
      throw new TypeCheckingVisitorException("Arrays are not allowed to be used with the " + operator + " operator.  The " + side.toString().toLowerCase() + " hand operator is an array of type " + type.tokenType, token);
    }
  }

  private boolean ensureExtendParentHasDefaultConstructor() {
    if (node.extendsList.size() == 0) return true;
    return node.extendsList.get(0).isDefaultConstructorVisibleToChildren();
  }

  private Declaration determineDeclaration(Name name, Class [] classes) throws VisitorException {
    Set<Class> classSet = new HashSet<Class>(Arrays.asList(classes));
    if(name.getDeclarationTypes() == null) {
      throw new TypeCheckingVisitorException("Found no declarations for " + name.getLexeme(), name);
    }

    for(Declaration declaration : name.getDeclarationTypes()) {
      if(classSet.contains(declaration.getClass())) {
        return declaration;
      }
    }

    throw new TypeCheckingVisitorException("Can not determine declaration " + name.getLexeme(), name);
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
      throw new TypeCheckingVisitorException("Can not determine declaration " + name.getLexeme(), name);
    }

    return declarations;
  }

  private Declaration matchCall(List<Token> matchingDeclarations, boolean isMethod, List<TypeCheckToken> argumentsToMethod, Token context) throws VisitorException {
    for (Token declaration : matchingDeclarations) {
      FormalParameterList parameterList;
      if(isMethod) {
        if(declaration instanceof MethodDeclaration) {
          parameterList = ((MethodDeclaration) declaration).methodHeader.paramList;
        } else {
          parameterList = ((AbstractMethodDeclaration) declaration).methodHeader.paramList;
        }
      } else {
        parameterList = ((ConstructorDeclaration) declaration).declarator.getParameterList();
      }

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

    throw new TypeCheckingVisitorException("Can not find any " + (isMethod ? "Method" : "Constructor") + " declaration for " + (context != null ? context.getLexeme() : "null"), context);
  }
}