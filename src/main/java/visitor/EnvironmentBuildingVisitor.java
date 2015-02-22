package visitor;

import exception.EnvironmentBuildingVisitorException;
import exception.VisitorException;
import symbol.SymbolTable;
import token.AbstractMethodDeclaration;
import token.ClassDeclaration;
import token.CompilationUnit;
import token.ConstructorDeclaration;
import token.Declaration;
import token.FieldDeclaration;
import token.FormalParameter;
import token.InterfaceDeclaration;
import token.LocalVariableDeclaration;
import token.MethodDeclaration;
import token.Token;

import java.util.List;

/**
 * Visitor responsible for building the global scope of the SymbolTable.
 */
public class EnvironmentBuildingVisitor extends BaseVisitor {
  private SymbolTable table;
  private StringBuilder prefix;

  private boolean isDefaultPackage = false;

  public EnvironmentBuildingVisitor(SymbolTable table) {
    this.table = table;
    this.prefix = new StringBuilder();
  }

  public void buildGlobalScope(List<CompilationUnit> units) throws VisitorException {
    table.newScope();
    for (CompilationUnit unit : units) {
      unit.acceptReverse(this);
    }
  }

  @Override
  public void visit(CompilationUnit token) throws VisitorException {
    super.visit(token);
    prefix.setLength(0);
    isDefaultPackage = false;
    // Add the package as the prefix.
    if (token.packageDeclaration != null) {
      if (!table.contains(token.packageDeclaration.getIdentifier())) {
        addDecl(token.packageDeclaration.getIdentifier(), token.packageDeclaration);
      }
      prefix.append(token.packageDeclaration.getIdentifier() + ".");
    } else {
      isDefaultPackage = true;
    }
    // Add the Class or Interface declaration to the symbol table, and set as prefix.
    Declaration decl = token.typeDeclaration.getDeclaration();
    String identifier = prefix.toString() + decl.getIdentifier();
    prefix.append(decl.getIdentifier() + ".");
    if (table.containsAnyOfType(identifier, ClassDeclaration.class) ||
        table.containsAnyOfType(identifier, InterfaceDeclaration.class)) {
      throw new EnvironmentBuildingVisitorException(
          "Error: No two classes or interfaces have the same canonical name.", token);
    }
    addDecl(identifier, decl);
  }

  @Override
  public void visit(FieldDeclaration token) throws VisitorException {
    super.visit(token);
    String identifier = prefix.toString() + token.getIdentifier();
    if (table.containsAnyOfType(identifier, FieldDeclaration.class)) {
      throw new EnvironmentBuildingVisitorException(
          "Error: No two fields declared in the same class may have the same name.", token);
    }
    addDecl(identifier, token);
  }

  @Override
  public void visit(MethodDeclaration token) throws VisitorException {
    super.visit(token);
    String identifier = prefix.toString() + token.getIdentifier();
    addDecl(identifier, token);
  }

  @Override
  public void visit(ConstructorDeclaration token) throws VisitorException {
    super.visit(token);
    String identifier = prefix.toString() + token.getIdentifier();
    addDecl(identifier, token);
  }

  @Override
  public void visit(AbstractMethodDeclaration token) throws VisitorException {
    super.visit(token);
    String identifier = prefix.toString() + token.getIdentifier();
    addDecl(identifier, token);
  }

  @Override
  public void visit(FormalParameter token) throws VisitorException {
    super.visit(token);
    String identifier = token.getIdentifier();
    if (table.containsAnyOfType(identifier, FormalParameter.class)) {
      throw new EnvironmentBuildingVisitorException(
          "No two local variables with overlapping scope have the same name.", token);
    }
    table.addDecl(identifier, token);
  }

  @Override
  public void visit(LocalVariableDeclaration token) throws VisitorException {
    super.visit(token);
    String identifier = token.getIdentifier();
    if (table.containsAnyOfType(identifier, LocalVariableDeclaration.class) ||
        table.containsAnyOfType(identifier, FormalParameter.class)) {
      throw new EnvironmentBuildingVisitorException(
          "No two local variables with overlapping scope have the same name.", token);
    }
    table.addDecl(identifier, token);
  }

  @Override
  public void visit(Token token) throws VisitorException {
    super.visit(token);
    if (token.getLexeme().equals("{")) {
      table.newScope();
    } else if (token.getLexeme().equals("}")) {
      table.deleteScope();
    }
  }

  private void addDecl(String identifier, Declaration decl) {
    if (!isDefaultPackage) table.addDecl(identifier, decl);
  }
}
