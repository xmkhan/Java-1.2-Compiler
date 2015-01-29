package visitor;

import token.*;

import java.util.*;

public class GenericCheckVisitor extends BaseVisitor {

  private String fileName;

  public GenericCheckVisitor(String fileName) {
    this.fileName = fileName;
  }

  @Override
  public void visit(Modifiers token) throws VisitorException{
    super.visit(token);

    // Check for duplicate modifiers
    Set<Modifier> uniqueSet = new HashSet<>(token.getModifiers());
    if(uniqueSet.size() != token.getModifiers().size()) {
      throw new VisitorException("Detected duplicate modifiers: " + Arrays.toString(token.getModifiers().toArray()));
    }
  }

  @Override
  public void visit(ClassBody token) throws VisitorException {
    super.visit(token);

    // Check for an class body declaration has atleast one constructor
    boolean hasConstructor = false;
    for (ClassBodyDeclaration bodyDeclaration : token.bodyDeclarations.getBodyDeclarations()) {
      if(bodyDeclaration.declaration instanceof ConstructorDeclaration) {
        hasConstructor = true;
        break;
      }
    }

    if(!hasConstructor) {
      throw new VisitorException("Class requires atleast 1 constructor");
    }
  }

  @Override
  public void visit(ClassDeclaration token) throws VisitorException {
    super.visit(token);

    HashSet<TokenType> modifierTypes = getModifierTypesAsSet(token.modifiers);

    // Check class has same identifier as file name
    if(!token.identifier.getLexeme().equals(fileName)) {
      throw new VisitorException("Expected Class " + fileName + " but found Class " + token.identifier.getLexeme());
    }

    // Check for that a class can not be both abstract and final
    if(modifierTypes.contains(TokenType.ABSTRACT) && modifierTypes.contains(TokenType.FINAL)) {
      throw new VisitorException("Class " + token.identifier.getLexeme() + " can not be both Abstract and Final");
    }
  }

  @Override
  public void visit(InterfaceDeclaration token) throws VisitorException {
    super.visit(token);

    // Check class has same identifier as file name
    if(!token.identifier.getLexeme().equals(fileName)) {
      throw new VisitorException("Expected Interface " + fileName + " but found Interface " + token.identifier.getLexeme());
    }
  }

  @Override
  public void visit(AbstractMethodDeclaration token) throws VisitorException {
    super.visit(token);
    HashSet<TokenType> modifierTypes = getModifierTypesAsSet(token.methodHeader.modifiers);

    // Check for an interface method can not be static, final, or native.
    if(modifierTypes.contains(TokenType.NATIVE) || modifierTypes.contains(TokenType.STATIC) ||
            modifierTypes.contains(TokenType.FINAL)) {
      throw new VisitorException("Interface method " + token.methodHeader.methodDeclarator.getLexeme() +
              " can not be static, final, or native.");
    }
  }

  @Override
  public void visit(MethodDeclaration token) throws VisitorException {
    super.visit(token);
    HashSet<TokenType> modifierTypes = getModifierTypesAsSet(token.methodHeader.modifiers);

    // Check for method has a body if and only if it is neither abstract nor native.
    if((token.methodBody == null && !modifierTypes.contains(TokenType.ABSTRACT) && !modifierTypes.contains(TokenType.NATIVE))
            || (token.methodBody != null && (modifierTypes.contains(TokenType.ABSTRACT) || modifierTypes.contains(TokenType.NATIVE)))) {
      throw new VisitorException("Method " + token.methodHeader.methodDeclarator.getLexeme() +
              " has a body and is abstract or native or doesn't have a body and is not abstract nor native.");
    }

    // Check for an abstract method cannot be static or final.
    if(modifierTypes.contains(TokenType.ABSTRACT) && (modifierTypes.contains(TokenType.STATIC) || modifierTypes.contains(TokenType.FINAL))) {
      throw new VisitorException("Method " + token.methodHeader.methodDeclarator.getLexeme() +
              " can not be abstract and be static or final.");
    }

    // Check for a static method can not be final
    if(modifierTypes.contains(TokenType.STATIC) && modifierTypes.contains(TokenType.FINAL)) {
      throw new VisitorException("Method " + token.methodHeader.methodDeclarator.getLexeme() +
              " can not be static and final.");
    }

    // Check for a native method must be static
    if(modifierTypes.contains(TokenType.NATIVE) && !modifierTypes.contains(TokenType.STATIC)) {
      throw new VisitorException("Method " + token.methodHeader.methodDeclarator.getLexeme() +
              " has to be static due to it is native.");
    }
  }

  @Override
  public void visit(FieldDeclaration token) throws VisitorException {
    super.visit(token);

    HashSet<TokenType> modifierTypes = getModifierTypesAsSet(token.modifiers);
    // Check for no field declarations can be final.
    if(modifierTypes.contains(TokenType.FINAL)) {
      throw new VisitorException("Identifier " + token.variableDeclarator.getLexeme() +
              " can not be declared final.");
    }
  }

  private HashSet<TokenType> getModifierTypesAsSet(Modifiers modifiers) {
    HashSet<TokenType> modifierSet = new HashSet<>();

    if(modifiers == null) {
      return modifierSet;
    }

    for(Modifier modifier : modifiers.getModifiers()) {
      modifierSet.add(modifier.getTokenType());
    }
    return modifierSet;
  }
}