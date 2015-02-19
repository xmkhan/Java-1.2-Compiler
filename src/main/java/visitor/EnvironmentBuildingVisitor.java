package visitor;

import exception.EnvironmentBuildingException;
import exception.VisitorException;
import symbol.SymbolTable;
import token.AbstractMethodDeclaration;
import token.ClassDeclaration;
import token.CompilationUnit;
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

  public EnvironmentBuildingVisitor(SymbolTable table) {
    this.table = table;
    this.prefix = new StringBuilder();
  }

  public void buildGlobalScope(List<CompilationUnit> units) throws VisitorException {
    table.newScope();
    for (CompilationUnit unit : units) {
      unit.accept(this);
    }
  }

  @Override
  public void visit(CompilationUnit token) throws VisitorException {
    super.visit(token);
    prefix.setLength(0);
    // Add the package as the prefix.
    if (token.packageDeclaration != null) {
      prefix.append(token.packageDeclaration.getLexeme() + ".");
    }
    // Add the Class or Interface declaration to the symbol table, and set as prefix.
    Declaration decl = token.typeDeclaration.getDeclaration();
    prefix.append(decl.getIdentifier());
    String identifier = prefix.toString();
    if (table.containsAnyOfType(identifier, ClassDeclaration.class) ||
        table.containsAnyOfType(identifier, InterfaceDeclaration.class)) {
      throw new EnvironmentBuildingException(
          "Error: No two classes or interfaces have the same canonical name.", token);
    }
    table.addDecl(identifier, decl);
  }

  @Override
  public void visit(FieldDeclaration token) throws VisitorException {
    super.visit(token);
    String identifier = prefix.toString() + token.getIdentifier();
    if (table.containsAnyOfType(identifier, FieldDeclaration.class)) {
      throw new EnvironmentBuildingException(
          "Error: No two fields declared in the same class may have the same name.", token);
    }
    table.addDecl(identifier, token);
  }

  @Override
  public void visit(MethodDeclaration token) throws VisitorException {
    super.visit(token);
    String identifier = prefix.toString() + token.getIdentifier();
    table.addDecl(identifier, token);
    // Manually remove arguments because they are before the "{" so "}" will not remove them from the Scope.
    // The reason for this is that Visitors are performing an in-order traversal.
    if (token.methodHeader.paramList != null) {
      List<FormalParameter> params = token.methodHeader.paramList.params;
      for (FormalParameter param : params) {
        table.removeDecl(param.getIdentifier(), param);
      }
    }
  }

  @Override
  public void visit(AbstractMethodDeclaration token) throws VisitorException {
    super.visit(token);
    String identifier = prefix.toString() + token.getIdentifier();
    table.addDecl(identifier, token);
  }

  @Override
  public void visit(FormalParameter token) throws VisitorException {
    super.visit(token);
    String identifier = token.getIdentifier();
    if (table.containsAnyOfType(identifier, FormalParameter.class)) {
      throw new EnvironmentBuildingException(
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
      throw new EnvironmentBuildingException(
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
}