package algorithm.name.resolution;

import exception.VariableNameResolutionException;
import symbol.SymbolTable;
import token.ClassDeclaration;
import token.CompilationUnit;
import token.Declaration;
import token.FieldDeclaration;
import token.ImportDeclaration;
import token.MethodDeclaration;
import token.Name;
import token.Token;
import token.Type;
import type.hierarchy.HierarchyGraph;
import type.hierarchy.HierarchyGraphNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Variable resolution algorithm for checking variable
 */
public class VariableNameResolutionAlgorithm {
  private SymbolTable symbolTable;
  private SymbolTable variableTable;
  private HierarchyGraph hierarchyGraph;

  public VariableNameResolutionAlgorithm(SymbolTable symbolTable, SymbolTable variableTable, HierarchyGraph hierarchyGraph) {
    this.symbolTable = symbolTable;
    this.variableTable = variableTable;
    this.hierarchyGraph = hierarchyGraph;
  }

  public void resolveName(CompilationUnit unit, Name name) throws VariableNameResolutionException {
    if (name.isSimple()) {
      resolveSingleNameDeclarations(unit, name);
    } else {
      resolveQualifiedNameDeclarations(unit, name);
    }
  }

  private void resolveSingleNameDeclarations(CompilationUnit unit, Name name) throws VariableNameResolutionException {
    HierarchyGraphNode node = hierarchyGraph.get(unit.typeDeclaration.getDeclaration().getAbsolutePath());
    List<Declaration> declarations = new ArrayList<Declaration>();

    // 1. Check if it is a local variable, method param, or field.
    List<Token> variableSymbols = variableTable.find(name.getLexeme());
    if (!variableSymbols.isEmpty()) {
      declarations.add((Declaration) variableSymbols.get(0));
    }

    // 2. Check the object hierarchy
    List<MethodDeclaration> classMethods = node.getAllMethods();
    for (MethodDeclaration method : classMethods) {
      if (method.getIdentifier().equals(name.getLexeme())) {
        declarations.add(method);

      }
    }
    List<FieldDeclaration> classFields = node.getAllFields();
    for (FieldDeclaration field : classFields) {
        if (field.getIdentifier().equals(name.getLexeme())) {
          declarations.add(field);
        }
    }

    // 3. Check single import
    List<ImportDeclaration> importDeclarations = unit.importDeclarations.getAllImportsWithSuffix(name.getLexeme());
    if (!importDeclarations.isEmpty()) {
      String absolutePathToType = importDeclarations.get(0).getLexeme() + '.' + name.getLexeme();
      Declaration declaration = (Declaration) symbolTable.findWithType(absolutePathToType, ClassDeclaration.class);
      if (declaration != null) {
        declarations.add(declaration);
      }
    }

    // 4. Check on-demand import
    int matches = 0;
    List<ImportDeclaration> onDemandImportDeclarations = unit.importDeclarations.getAllOnDemandImports();
    for (ImportDeclaration importDeclaration : onDemandImportDeclarations) {
      String absolutePathToType = importDeclaration.getLexeme() + '.' + name.getLexeme();
      Declaration declaration = (Declaration) symbolTable.findWithType(absolutePathToType, ClassDeclaration.class);
      if (declaration != null) {
        declarations.add(declaration);
        matches++;
      }
    }
    if (matches > 1) throw new VariableNameResolutionException("Multiple on-demand imports found");
    name.setDeclarationTypes(declarations);
  }

  private void resolveQualifiedNameDeclarations(CompilationUnit unit, Name name) throws VariableNameResolutionException {
    String[] identifiers = name.getLexeme().split("\\.");

    StringBuilder currentType = new StringBuilder();
    Declaration lastMatchedDecl;
    for (int i = 0; i < identifiers.length - 1; ++i) {
      if (i > 0) currentType.append('.');
      currentType.append(identifiers[i]);
      boolean match = false;
      // 1. Check if it is a local variable, method param, or field.
      List<Token> variableSymbols = variableTable.find(currentType.toString());
      if (!variableSymbols.isEmpty()) {
        lastMatchedDecl = (Declaration) variableSymbols.get(0);
        currentType.setLength(0);
        currentType.append(getTypePath(lastMatchedDecl.type);
        continue;
      }

      // 2. Check single import
      List<ImportDeclaration> importDeclarations = unit.importDeclarations.getAllImportsWithSuffix(currentType.toString());
      if (!importDeclarations.isEmpty()) {
        String absolutePathToType = importDeclarations.get(0).getLexeme() + '.' + currentType.toString();
        lastMatchedDecl = (Declaration) symbolTable.findWithType(absolutePathToType, ClassDeclaration.class);
        if (lastMatchedDecl != null) {
          currentType.setLength(0);
          currentType.append(lastMatchedDecl.getAbsolutePath());
        }
      }

      // 4. Check on-demand import
      int matches = 0;
      List<ImportDeclaration> onDemandImportDeclarations = unit.importDeclarations.getAllOnDemandImports();
      for (ImportDeclaration importDeclaration : onDemandImportDeclarations) {
        String absolutePathToType = importDeclaration.getLexeme() + '.' + name.getLexeme();
        lastMatchedDecl = (Declaration) symbolTable.findWithType(absolutePathToType, ClassDeclaration.class);
        if (lastMatchedDecl != null) {
          currentType.setLength(0);
          currentType.append(lastMatchedDecl.getAbsolutePath());
          matches++;
        }
      }

      // 3. Check the object hierarchy
      HierarchyGraphNode node = hierarchyGraph.get(currentType.toString());
      List<FieldDeclaration> classFields = node.getAllFields();
      for (FieldDeclaration field : classFields) {
        if (field.getIdentifier().equals(identifiers[i])) {
          currentType.setLength(0);
          currentType.append(getTypePath(field.type));
          match = true;
          break;
        }
      }
      if (!match || matches > 1) {
        throw new VariableNameResolutionException("Unable to resolve part of type", name);
      }
    }
    List<Declaration> declarations = new ArrayList<Declaration>();

    // Change edge case for array[] types.
    if (currentType.toString().contains("[]") && !identifiers[identifiers.length - 1].equals("length")) {
      throw new VariableNameResolutionException("No Array[] field found", name);
    }

    // Fill in all matching declarations.
    HierarchyGraphNode node = hierarchyGraph.get(currentType.toString());
    List<MethodDeclaration> classMethods = node.getAllMethods();
    List<FieldDeclaration> classFields = node.getAllFields();
    for (MethodDeclaration method : classMethods) {
      if (method.getIdentifier().equals(name.getLexeme())) {
        declarations.add(method);

      }
    }
    for (FieldDeclaration field : classFields) {
      if (field.getIdentifier().equals(name.getLexeme())) {
        declarations.add(field);
      }
    }
    name.setDeclarationTypes(declarations);
  }

  private String getTypePath(Type type) throws VariableNameResolutionException {
    if (type.primitiveType != null) {
      throw new VariableNameResolutionException("Cannot get type path for primitive", type);
    }
    if (type.referenceType.arrayType != null) {
      return "[]";
    } else {
      return type.referenceType.classOrInterfaceType.name.getAbsolutePath();
    }
  }

}