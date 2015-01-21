package token;

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
  INT_LITERAL("int_literal"),
  STR_LITERAL("str_literal"),
  IMPORT_NAME("import_name"),
  COMPOUND_NAME("compound_name"),
  IDENTIFIER("identifier");

  private final String name;

  private TokenType(String name) {
    this.name = name;
  }
  @Override
  public String toString(){
    return name;
  }
}