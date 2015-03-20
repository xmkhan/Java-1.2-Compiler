package visitor;

import algorithm.name.resolution.VariableNameResolutionAlgorithm;
import exception.DisambiguityVisitorException;
import exception.VariableNameResolutionException;
import exception.VisitorException;
import symbol.SymbolTable;
import token.CompilationUnit;
import token.Declaration;
import token.FieldDeclaration;
import token.LeftHandSide;
import token.Name;
import token.Token;
import type.hierarchy.HierarchyGraph;

import java.util.List;

/**
 * Responsible for resolving ambiguity in type names.
 */
public class DisambiguityVisitor extends VariableScopeVisitor {

  private final HierarchyGraph graph;
  private final SymbolTable table;
  private final VariableNameResolutionAlgorithm resolutionAlgm;
  private CompilationUnit unit;
  private FieldDeclaration mostRecentField;
  private boolean isLeftHandSide = false;

  public DisambiguityVisitor(SymbolTable symbolTable, HierarchyGraph graph) {
    super();
    this.table = symbolTable;
    this.graph = graph;
    resolutionAlgm = new VariableNameResolutionAlgorithm(symbolTable, getVariableTable(), graph);
  }

  public void disambiguateUnits(List<CompilationUnit> units) throws VisitorException {
    for (CompilationUnit unit : units) {
      getVariableTable().newScope();
      unit.acceptReverse(this);
      getVariableTable().deleteScope();
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
    if (isLeftHandSide) {
      isLeftHandSide = false;
      Declaration fieldToken = (Declaration) table.findWithType(
          unit.typeDeclaration.getDeclaration().getAbsolutePath() + '.' + token.getLexeme(),
          new Class[] {FieldDeclaration.class});
      if (fieldToken != null) getVariableTable().addDecl(fieldToken.getIdentifier(), fieldToken);
    }
    try {
      resolutionAlgm.resolveName(unit, token, mostRecentField);
    } catch (VariableNameResolutionException e) {
      e.printStackTrace();
      throw new DisambiguityVisitorException(e.getMessage(), token);
    }
  }

  @Override
  public void visit(FieldDeclaration token) throws VisitorException {
    super.visit(token);
    mostRecentField = token;
  }

  @Override
  public void visit(LeftHandSide token) throws VisitorException {
    super.visit(token);
    isLeftHandSide = true;
  }

  @Override
  public void visit(Token token) throws VisitorException {
    super.visit(token);
    if (token.getLexeme().equals(";")) {
      mostRecentField = null;
    }
  }
}
