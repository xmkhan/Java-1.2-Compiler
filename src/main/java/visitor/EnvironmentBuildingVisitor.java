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
import token.PackageDeclaration;
import token.Token;

import java.util.List;

/**
 * Visitor responsible for building the global scope of the SymbolTable.
 */
public class EnvironmentBuildingVisitor extends BaseVisitor {
  private SymbolTable table;
  private StringBuilder prefix;

  private int classId;
  private int interfaceMethodId;

  public EnvironmentBuildingVisitor(SymbolTable table) {
    this.table = table;
    this.prefix = new StringBuilder();
  }

  public void buildGlobalScope(List<CompilationUnit> units) throws VisitorException {
    classId = 0;
    interfaceMethodId = 0;
    table.newScope();
    for (CompilationUnit unit : units) {
      unit.acceptReverse(this);
    }
  }

  public int getInterfaceMethodCount() {
    return interfaceMethodId;
  }

  @Override
  public void visit(CompilationUnit token) throws VisitorException {
    super.visit(token);
    prefix.setLength(0);
    // Add the package as the prefix.
    if (token.packageDeclaration != null) {
      if (!table.containsAnyOfType(token.packageDeclaration.getIdentifier(), new Class[]{PackageDeclaration.class})) {
        table.addDecl(token.packageDeclaration.getIdentifier(), token.packageDeclaration);
      }
      prefix.append(token.packageDeclaration.getIdentifier() + ".");
    }
    // Add the Class or Interface declaration to the symbol table, and set as prefix.
    Declaration decl = token.typeDeclaration.getDeclaration();
    String identifier = prefix.toString() + decl.getIdentifier();
    prefix.append(decl.getIdentifier() + ".");
    if (table.containsAnyOfType(identifier, new Class[] {ClassDeclaration.class, InterfaceDeclaration.class})) {
      throw new EnvironmentBuildingVisitorException(
          "Error: No two classes or interfaces have the same canonical name.", token);
    }
    table.addDecl(identifier, decl);
  }

  @Override
  public void visit(FieldDeclaration token) throws VisitorException {
    super.visit(token);
    String identifier = prefix.toString() + token.getIdentifier();
    if (table.containsAnyOfType(identifier, new Class[] {FieldDeclaration.class})) {
      throw new EnvironmentBuildingVisitorException(
          "Error: No two fields declared in the same class may have the same name.", token);
    }
    table.addDecl(identifier, token);
  }

  @Override
  public void visit(MethodDeclaration token) throws VisitorException {
    super.visit(token);
    String identifier = prefix.toString() + token.getIdentifier();
    table.addDecl(identifier, token);
  }

  @Override
  public void visit(ConstructorDeclaration token) throws VisitorException {
    super.visit(token);
    String identifier = prefix.toString() + token.getIdentifier();
    table.addDecl(identifier, token);
  }

  @Override
  public void visit(AbstractMethodDeclaration token) throws VisitorException {
    super.visit(token);
    String identifier = prefix.toString() + token.getIdentifier();
    table.addDecl(identifier, token);
    token.interfaceMethodId = interfaceMethodId++;
  }

  @Override
  public void visit(FormalParameter token) throws VisitorException {
    super.visit(token);
    String identifier = token.getIdentifier();
    if (table.containsAnyOfType(identifier, new Class[] {FormalParameter.class})) {
      throw new EnvironmentBuildingVisitorException(
          "No two local variables with overlapping scope have the same name.", token);
    }
    table.addDecl(identifier, token);
  }

  @Override
  public void visit(LocalVariableDeclaration token) throws VisitorException {
    super.visit(token);
    String identifier = token.getIdentifier();
    if (table.containsAnyOfType(identifier, new Class[] {LocalVariableDeclaration.class, FormalParameter.class})) {
      throw new EnvironmentBuildingVisitorException(
          "No two local variables with overlapping scope have the same name.", token);
    }
    table.addDecl(identifier, token);
  }

  @Override
  public void visit(ClassDeclaration token) throws VisitorException {
    super.visit(token);
    token.classId = classId++;
  }

  @Override
  public void visit(InterfaceDeclaration token) throws VisitorException {
    super.visit(token);
    token.classId = classId++;
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
}
