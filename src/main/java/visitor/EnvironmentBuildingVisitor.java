package visitor;

import exception.EnvironmentBuildingException;
import symbol.SymbolTable;
import token.ClassBodyDeclarations;
import token.CompilationUnit;

import java.util.List;

/**
 * Visitor responsible for building the global scope of the SymbolTable.
 */
public class EnvironmentBuildingVisitor extends BaseVisitor {
  private SymbolTable table;

  public EnvironmentBuildingVisitor(SymbolTable table) {
    this.table = table;
  }

  public void buildGlobalScope(List<CompilationUnit> units) throws EnvironmentBuildingException {
    table.newScope();
    for (CompilationUnit unit : units) {
      StringBuilder sb = new StringBuilder();
      if (unit.packageDeclaration != null) sb.append(unit.packageDeclaration.name.getLexeme() + ".");
      sb.append(unit.typeDeclaration.getDeclaration().getIdentifier());
      // 1. Add the class declaration.
      table.addDecl(sb.toString(), unit.typeDeclaration.getDeclaration());
      if (unit.typeDeclaration.classDeclaration != null) {
        ClassBodyDeclarations classBodyDeclarations = unit.typeDeclaration.classDeclaration
      } else {

      }
    }

  }
}
