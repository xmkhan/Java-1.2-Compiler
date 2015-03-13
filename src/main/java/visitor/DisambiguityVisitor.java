package visitor;

import algorithm.name.resolution.VariableNameResolutionAlgorithm;
import exception.DisambiguityVisitorException;
import exception.VariableNameResolutionException;
import exception.VisitorException;
import symbol.SymbolTable;
import token.CompilationUnit;
import token.FieldDeclaration;
import token.Name;
import token.Token;
import type.hierarchy.HierarchyGraph;

import java.util.List;

/**
 * Responsible for resolving ambiguity in type names.
 */
public class DisambiguityVisitor extends VariableScopeVisitor {

  private final HierarchyGraph graph;
  private final VariableNameResolutionAlgorithm resolutionAlgm;
  private CompilationUnit unit;
  private FieldDeclaration mostRecentField;

  public DisambiguityVisitor(SymbolTable symbolTable, HierarchyGraph graph) {
    super(symbolTable);
    this.graph = graph;
    resolutionAlgm = new VariableNameResolutionAlgorithm(getSymbolTable(), getVariableTable(), graph);
  }

  public void disambiguateUnits(List<CompilationUnit> units) throws VisitorException {
    for (CompilationUnit unit : units) {
      unit.acceptReverse(this);
    }
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
      resolutionAlgm.resolveName(unit, token, mostRecentField);
    } catch (VariableNameResolutionException e) {
      e.printStackTrace();
      throw new DisambiguityVisitorException("Failed to resolve name: " + token.getLexeme(), token);
    }
  }

  @Override
  public void visit(FieldDeclaration token) throws VisitorException {
    super.visit(token);
    mostRecentField = token;
  }

  @Override
  public void visit(Token token) throws VisitorException {
    super.visit(token);
    if (token.getLexeme().equals(";")) {
      mostRecentField = null;
    }
  }
}
