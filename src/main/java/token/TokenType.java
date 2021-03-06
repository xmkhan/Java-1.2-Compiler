package token;

import java.util.HashMap;

/**
 * Enum for TokenType.
 */
public enum TokenType {
  // Literals
  BOOLEAN_TRUE("true"),
  BOOLEAN_FALSE("false"),
  NULL("null"),
  THIS("this"),
  SUPER("super"),
  NEW("new"),
  INSTANCEOF("instanceof"),
  RETURN("return"),
  // Control flow
  IF("if"),
  ELSE("else"),
  WHILE("while"),
  FOR("for"),
  // Java keywords
  PACKAGE("package"),
  IMPORT("import"),
  // Other Java keywords (not implemented)
  BREAK("break"),
  CASE("case"),
  CATCH("catch"),
  CONST("const"),
  CONTINUE("continue"),
  DEFAULT("default"),
  DO("do"),
  FINALLY("finally"),
  FLOAT("float"),
  GOTO("goto"),
  LONG("long"),
  STRICTFP("strictfp"),
  SWITCH("switch"),
  SYNCHRONIZED("synchronized"),
  THROW("throw"),
  THROWS("throws"),
  TRANSIENT("transient"),
  TRY("try"),
  VOLATILE("volatile"),
  DOUBLE("double"),
  // Modifiers
  FINAL("final"),
  STATIC("static"),
  NATIVE("native"),
  PUBLIC("public"),
  PROTECTED("protected"),
  PRIVATE("private"),
  ABSTRACT("abstract"),
  // Class literals and modifiers
  CLASS("class"),
  INTERFACE("interface"),
  EXTENDS("extends"),
  IMPLEMENTS("implements"),
  // Comments
  COMMENT_STAR("/* */"),
  COMMENT_SLASH("//"),
  // Primitive types
  BOOLEAN("boolean"),
  INT("int"),
  CHAR("char"),
  BYTE("byte"),
  SHORT("short"),
  ARRAY("array"),
  VOID("void"),
  // Bitwise operators
  BITWISE_OR("|"),
  BITWISE_AND("&"),
  BITWISE_NEG("!"),
  // Operators
  PLUS_OP("+"),
  MINUS_OP("-"),
  MULT_OP("*"),
  DIV_OP("/"),
  MOD_OP("%"),
  // Infix operators
  OR_OP("||"),
  AND_OP("&&"),
  EQUALITY_OP("=="),
  NEG_EQUALITY_OP("!="),
  LESS_THAN_OP("<"),
  LESS_THAN_OP_EQUAL("<="),
  MORE_THAN_OP(">"),
  MORE_THAN_OP_EQUAL(">="),
  // Assignment operators
  EQUAL("="),
  // Punctuation
  DOT("."),
  COMMA(","),
  SEMI_COLON(";"),
  LEFT_PARAN("("),
  RIGHT_PARAN(")"),
  LEFT_BRACE("{"),
  RIGHT_BRACE("}"),
  LEFT_BRACKET("["),
  RIGHT_BRACKET("]"),
  DOUBLE_QUOTE("\""),
  SINGLE_QUOTE("\'"),
  // Non-reserved keywords
  RESERVED_LENGTH("reserved_length"),
  BOF("beginning_of_file"),
  EOF("EOF"),
  INT_LITERAL("IntLiteral"),
  BOOLEAN_LITERAL("BooleanLiteral"),
  CHAR_LITERAL("CharLiteral"),
  STR_LITERAL("StringLiteral"),
  IDENTIFIER("Identifier"),
  // Non-terminals
  ExpressionStatement("ExpressionStatement"),
  FieldDeclaration("FieldDeclaration"),
  PackageDeclaration("PackageDeclaration"),
  InterfaceMemberDeclarations("InterfaceMemberDeclarations"),
  MethodInvocation("MethodInvocation"),
  Interfaces("Interfaces"),
  ClassMemberDeclaration("ClassMemberDeclaration"),
  WhileStatement("WhileStatement"),
  Modifier("Modifier"),
  Primary("Primary"),
  IfThenStatement("IfThenStatement"),
  Modifiers("Modifiers"),
  InterfaceDeclaration("InterfaceDeclaration"),
  SingleTypeImportDeclaration("SingleTypeImportDeclaration"),
  CastExpression("CastExpression"),
  ImportDeclaration("ImportDeclaration"),
  InterfaceBody("InterfaceBody"),
  ClassBodyDeclaration("ClassBodyDeclaration"),
  ClassOrInterfaceType("ClassOrInterfaceType"),
  QualifiedName("QualifiedName"),
  IfThenElseStatementNoShortIf("IfThenElseStatementNoShortIf"),
  InclusiveOrExpression("InclusiveOrExpression"),
  ClassBody("ClassBody"),
  ForStatement("ForStatement"),
  TypeDeclarations("TypeDeclarations"),
  ConditionalAndExpression("ConditionalAndExpression"),
  TypeImportOnDemandDeclaration("TypeImportOnDemandDeclaration"),
  AssignmentOperator("AssignmentOperator"),
  Literal("Literal"),
  AndExpression("AndExpression"),
  SimpleName("SimpleName"),
  ArrayCreationExpression("ArrayCreationExpression"),
  IfThenElseStatement("IfThenElseStatement"),
  TypeDeclaration("TypeDeclaration"),
  ConditionalOrExpression("ConditionalOrExpression"),
  ConstructorDeclaration("ConstructorDeclaration"),
  LocalVariableDeclaration("LocalVariableDeclaration"),
  InterfaceTypeList("InterfaceTypeList"),
  ReturnStatement("ReturnStatement"),
  UnaryExpressionNotMinus("UnaryExpressionNotMinus"),
  RelationalExpression("RelationalExpression"),
  StatementNoShortIf("StatementNoShortIf"),
  BlockStatement("BlockStatement"),
  MethodDeclarator("MethodDeclarator"),
  UnaryExpression("UnaryExpression"),
  AdditiveExpression("AdditiveExpression"),
  ClassInstanceCreationExpression("ClassInstanceCreationExpression"),
  MethodDeclaration("MethodDeclaration"),
  AbstractMethodDeclaration("AbstractMethodDeclaration"),
  MultiplicativeExpression("MultiplicativeExpression"),
  FormalParameterList("FormalParameterList"),
  WhileStatementNoShortIf("WhileStatementNoShortIf"),
  VariableDeclarator("VariableDeclarator"),
  StatementWithoutTrailingSubstatement("StatementWithoutTrailingSubstatement"),
  AssignmentExpression("AssignmentExpression"),
  ReferenceType("ReferenceType"),
  Type("Type"),
  FormalParameter("FormalParameter"),
  Statement("Statement"),
  CompilationUnit("CompilationUnit"),
  Assignment("Assignment"),
  LeftHandSide("LeftHandSide"),
  ConstructorDeclarator("ConstructorDeclarator"),
  Super("Super"),
  MethodHeader("MethodHeader"),
  PrimitiveType("PrimitiveType"),
  BlockStatements("BlockStatements"),
  FieldAccess("FieldAccess"),
  StatementExpression("StatementExpression"),
  LocalVariableDeclarationStatement("LocalVariableDeclarationStatement"),
  ForInit("ForInit"),
  ArrayAccess("ArrayAccess"),
  Expression("Expression"),
  Block("Block"),
  ImportDeclarations("ImportDeclarations"),
  ArrayType("ArrayType"),
  InterfaceType("InterfaceType"),
  ForUpdate("ForUpdate"),
  EmptyStatement("EmptyStatement"),
  ClassType("ClassType"),
  Name("Name"),
  ExtendsInterfaces("ExtendsInterfaces"),
  ClassBodyDeclarations("ClassBodyDeclarations"),
  EqualityExpression("EqualityExpression"),
  ConstructorBody("ConstructorBody"),
  ClassDeclaration("ClassDeclaration"),
  InterfaceMemberDeclaration("InterfaceMemberDeclaration"),
  BooleanLiteral("BooleanLiteral"),
  ForStatementNoShortIf("ForStatementNoShortIf"),
  MethodBody("MethodBody"),
  ArgumentList("ArgumentList"),
  OBJECT("object"),
  NOT_USED("not_used");

  private final String name;

  private static HashMap<String, TokenType> reverseTokenTypeMap;

  public static TokenType getTokenType(String name) {
    if (reverseTokenTypeMap == null) {
      int size = TokenType.NOT_USED.ordinal();
      reverseTokenTypeMap = new HashMap<String, TokenType>(size);
      for (int i = 0; i < size; ++i) {
        TokenType token = TokenType.values()[i];
        reverseTokenTypeMap.put(token.toString(), token);
      }
    }
    return reverseTokenTypeMap.get(name);
  }

  private TokenType(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return name;
  }
}
