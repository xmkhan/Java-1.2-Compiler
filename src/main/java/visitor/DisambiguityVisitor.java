package visitor;

import algorithm.name.resolution.VariableNameResolutionAlgorithm;
import exception.DisambiguityVisitorException;
import exception.VariableNameResolutionException;
import exception.VisitorException;
import symbol.SymbolTable;
import token.CompilationUnit;
import token.Name;
import type.hierarchy.HierarchyGraph;

/**
 * Responsible for resolving ambiguity in type names.
 */
public class DisambiguityVisitor extends VariableScopeVisitor {

  private final HierarchyGraph graph;
  private final VariableNameResolutionAlgorithm resolutionAlgm;
  private CompilationUnit unit;

  public DisambiguityVisitor(SymbolTable symbolTable, HierarchyGraph graph) {
    super(symbolTable);
    this.graph = graph;
    resolutionAlgm = new VariableNameResolutionAlgorithm(getSymbolTable(), getVariableTable(), graph);
  }

  @Override
  public void visit(CompilationUnit token) throws VisitorException {
    super.visit(token);
    unit = token;
  }

  @Override
  public void visit(Name token) throws VisitorException {
    super.visit(token);
    try {
      resolutionAlgm.resolveName(unit, token);
    } catch (VariableNameResolutionException e) {
      e.printStackTrace();
      throw new DisambiguityVisitorException("Failed to resolve name: " + token.getLexeme(), token);
    }
  }
}
