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

import java.util.ArrayList;
import java.util.List;

/**
 * Responsible for resolving ambiguity in type names.
 */
public class DisambiguityVisitor extends VariableScopeVisitor {

  private final SymbolTable table;
  private final VariableNameResolutionAlgorithm resolutionAlgm;
  private CompilationUnit unit;
  private boolean skipResolution = false;

  public DisambiguityVisitor(SymbolTable symbolTable, HierarchyGraph graph) {
    super();
    this.table = symbolTable;
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
    if(skipResolution) {
      skipResolution = false;
      return;
    }
    try {
      resolutionAlgm.resolveName(unit, token);
    } catch (VariableNameResolutionException e) {
      throw new DisambiguityVisitorException(e.getMessage(), token);
    }
  }

  @Override
  public void visit(LeftHandSide token) throws VisitorException {
    super.visit(token);
    if (token.children.get(0) instanceof Name) {
      Name name = (Name) token.children.get(0);
      String fullName = String.format("%s.%s", unit.typeDeclaration.getDeclaration().getAbsolutePath(), name.getLexeme());
      List<Token> declarations = table.findWithPrefixOfAnyType(fullName, new Class[]{FieldDeclaration.class});
      if (declarations != null && !declarations.isEmpty()) {
        name.setDeclarationTypes(convertTokenToDeclaration(declarations));
        skipResolution = true;
      }
    }
  }

  private List<Declaration> convertTokenToDeclaration(List<Token> tokens) {
    List<Declaration> decls = new ArrayList<Declaration>();
    for (Token token : tokens) {
      decls.add(((Declaration) token));
    }
    return decls;
  }
}
