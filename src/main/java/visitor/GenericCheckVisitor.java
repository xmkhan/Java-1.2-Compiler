package visitor;

import exception.VisitorException;
import token.AbstractMethodDeclaration;
import token.CastExpression;
import token.ClassBody;
import token.ClassBodyDeclaration;
import token.ClassDeclaration;
import token.ConstructorDeclaration;
import token.FieldDeclaration;
import token.InterfaceDeclaration;
import token.MethodDeclaration;
import token.Modifier;
import token.Modifiers;
import token.MultiplicativeExpression;
import token.Token;
import token.TokenType;
import token.UnaryExpression;
import token.UnaryExpressionNotMinus;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class GenericCheckVisitor extends BaseVisitor {

  private String fileName;

  public GenericCheckVisitor(String fileName) {
    this.fileName = fileName.replaceFirst("[.][^.]+$", "");
  }

  @Override
  public void visit(Modifiers token) throws VisitorException {
    super.visit(token);

    // Check for atleast one modifier as we do not support package private
    // TODO: Convert to rule check
    if (token == null) {
      throw new VisitorException("Detected package private declaration", token);
    }

    // Check for duplicate modifiers
    Set<Modifier> uniqueSet = new HashSet<Modifier>(token.getModifiers());
    if (uniqueSet.size() != token.getModifiers().size()) {
      throw new VisitorException("Detected duplicate modifiers: " + Arrays.toString(token.getModifiers().toArray()), token);
    }

    // Check for if all fields, function, classes are at least public or protected
    HashSet<TokenType> modifierTypes = getModifierTypesAsSet(token);
    if (!modifierTypes.contains(TokenType.PUBLIC) && !modifierTypes.contains(TokenType.PROTECTED)) {
      throw new VisitorException("Detected package private declaration", token);
    }
  }

  @Override
  public void visit(ClassBody token) throws VisitorException {
    super.visit(token);

    // Check for an class body declaration has atleast one constructor
    boolean hasConstructor = false;
    for (ClassBodyDeclaration bodyDeclaration : token.bodyDeclarations.getBodyDeclarations()) {
      if (bodyDeclaration.declaration instanceof ConstructorDeclaration) {
        hasConstructor = true;
        break;
      }
    }

    if (!hasConstructor) {
      throw new VisitorException("Class requires at least 1 constructor", token);
    }
  }

  @Override
  public void visit(ClassDeclaration token) throws VisitorException {
    super.visit(token);

    HashSet<TokenType> modifierTypes = getModifierTypesAsSet(token.modifiers);

    // Check class has same identifier as file name
    if (!token.identifier.getLexeme().equals(fileName)) {
      throw new VisitorException("Expected Class " + fileName + " but found Class " + token.identifier.getLexeme(), token);
    }

    // Check for that a class can not be both abstract and final
    if (modifierTypes.contains(TokenType.ABSTRACT) && modifierTypes.contains(TokenType.FINAL)) {
      throw new VisitorException("Class " + token.identifier.getLexeme() + " can not be both Abstract and Final", token);
    }

    // Check for if class is declared as public as private classes and protected classes are not allowed
    if (!modifierTypes.contains(TokenType.PUBLIC)) {
      throw new VisitorException("Class " + token.identifier.getLexeme() + " needs to be declared public.", token);
    }
  }

  @Override
  public void visit(InterfaceDeclaration token) throws VisitorException {
    super.visit(token);

    // Check class has same identifier as file name
    if (!token.identifier.getLexeme().equals(fileName)) {
      throw new VisitorException("Expected Interface " + fileName + " but found Interface " + token.identifier.getLexeme(), token);
    }

    // Check for if class is declared as public as private classes and protected classes are not allowed
    HashSet<TokenType> modifierTypes = getModifierTypesAsSet(token.modifiers);
    if (!modifierTypes.contains(TokenType.PUBLIC)) {
      throw new VisitorException("Interface " + token.identifier.getLexeme() + " needs to be declared public.", token);
    }
  }

  @Override
  public void visit(AbstractMethodDeclaration token) throws VisitorException {
    super.visit(token);
    HashSet<TokenType> modifierTypes = getModifierTypesAsSet(token.methodHeader.modifiers);

    // Check for an interface method can not be static, final, or native.
    if (modifierTypes.contains(TokenType.NATIVE) || modifierTypes.contains(TokenType.STATIC) ||
        modifierTypes.contains(TokenType.FINAL)) {
      throw new VisitorException("Interface method " + token.methodHeader.identifier +
          " can not be static, final, or native.", token);
    }
  }

  @Override
  public void visit(MethodDeclaration token) throws VisitorException {
    super.visit(token);
    HashSet<TokenType> modifierTypes = getModifierTypesAsSet(token.methodHeader.modifiers);

    // Check for method has a body if and only if it is neither abstract nor native.
    if ((token.methodBody.isEmpty() && !modifierTypes.contains(TokenType.ABSTRACT) && !modifierTypes.contains(TokenType.NATIVE))
        || (!token.methodBody.isEmpty() && (modifierTypes.contains(TokenType.ABSTRACT) || modifierTypes.contains(TokenType.NATIVE)))) {
      throw new VisitorException("Method " + token.methodHeader.identifier +
          " has a body and is abstract or native or doesn't have a body and is not abstract nor native.", token);
    }

    // Check for an abstract method cannot be static or final.
    if (modifierTypes.contains(TokenType.ABSTRACT) && (modifierTypes.contains(TokenType.STATIC) || modifierTypes.contains(TokenType.FINAL))) {
      throw new VisitorException("Method " + token.methodHeader.identifier +
          " can not be abstract and be static or final.", token);
    }

    // Check for a static method can not be final
    if (modifierTypes.contains(TokenType.STATIC) && modifierTypes.contains(TokenType.FINAL)) {
      throw new VisitorException("Method " + token.methodHeader.identifier +
          " can not be static and final.", token);
    }

    // Check for a native method must be static
    if (modifierTypes.contains(TokenType.NATIVE) && !modifierTypes.contains(TokenType.STATIC)) {
      throw new VisitorException("Method " + token.methodHeader.identifier +
          " has to be static due to it is native.", token);
    }
  }

  @Override
  public void visit(FieldDeclaration token) throws VisitorException {
    super.visit(token);

    HashSet<TokenType> modifierTypes = getModifierTypesAsSet(token.modifiers);
    // Check for no field declarations can be final.
    if (modifierTypes.contains(TokenType.FINAL)) {
      throw new VisitorException("Identifier " + token.getIdentifier() +
          " can not be declared final.", token);
    }
  }

  @Override
  public void visit(CastExpression token) throws VisitorException {
    super.visit(token);

    if (token.isExpression() && !token.isName()) {
      throw new VisitorException("Invalid expression cast: " + token.getLexeme(), token);
    }

    // Integer validation check
    // 1. Get the underlying UnaryExpressionNotMinus expression.
    Token unaryExpr = token.children.get(token.children.size() - 1);
    UnaryExpressionNotMinus expr = null;
    if (unaryExpr instanceof UnaryExpressionNotMinus) {
      expr = (UnaryExpressionNotMinus) unaryExpr;
    } else if (unaryExpr instanceof UnaryExpression && !((UnaryExpression) unaryExpr).isNegative()) {
      expr = ((UnaryExpression) unaryExpr).posExp;
    }
    // 2. If the Literal is of IntLiteral, check the underlying integer.
    if (!isValidInteger(expr)) {
      throw new VisitorException("Not a valid integer at token: " + expr.literal.getLexeme(), token);
    }
  }

  @Override
  public void visit(MultiplicativeExpression token) throws VisitorException {
    super.visit(token);

    // Integer validation check
    if (!token.expr.isNegative() && !isValidInteger(token.expr.posExp)) {
      throw new VisitorException("Not a valid integer at token: " + token.expr.posExp.literal.getLexeme(), token);
    }
  }

  private boolean isValidInteger(UnaryExpressionNotMinus expr) {
    if (expr == null || !expr.isLiteral() || !expr.literal.isIntLiteral()) {
      return true;
    }
    String integer = expr.literal.getLexeme();
    try {
      Integer.parseInt(integer);
      return true;
    } catch (NumberFormatException e) {
      return false;
    }
  }

  private HashSet<TokenType> getModifierTypesAsSet(Modifiers modifiers) {
    HashSet<TokenType> modifierSet = new HashSet<TokenType>();

    if (modifiers == null) {
      return modifierSet;
    }

    for (Modifier modifier : modifiers.getModifiers()) {
      modifierSet.add(modifier.getModifier().getTokenType());
    }
    return modifierSet;
  }
}
