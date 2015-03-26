package algorithm.name.resolution;

import exception.VariableNameResolutionException;
import symbol.SymbolTable;
import token.BaseMethodDeclaration;
import token.ClassDeclaration;
import token.CompilationUnit;
import token.Declaration;
import token.FieldDeclaration;
import token.ImportDeclaration;
import token.InterfaceDeclaration;
import token.Name;
import token.PackageDeclaration;
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
  private final SymbolTable symbolTable;
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
    if (name.getDeclarationTypes() == null || name.getDeclarationTypes().isEmpty()) {
      throw new VariableNameResolutionException(
          "No Variable name resolution could be made for name:" + name.getLexeme() + " in class: " +
              unit.typeDeclaration.getDeclaration().getIdentifier(), name);
    }
  }

  private void resolveSingleNameDeclarations(CompilationUnit unit, Name name)
      throws VariableNameResolutionException {
    HierarchyGraphNode node = hierarchyGraph.get(unit.typeDeclaration.getDeclaration().getAbsolutePath());
    List<Declaration> declarations = new ArrayList<Declaration>();

    if (unit.typeDeclaration.getDeclaration().getIdentifier().equals(name.getLexeme())) {
      declarations.add(unit.typeDeclaration.getDeclaration());
    }
    // 1. Check if it is a local variable, method param.
    List<Token> variableSymbols = variableTable.find(name.getLexeme());
    if (!variableSymbols.isEmpty()) {
      declarations.add((Declaration) variableSymbols.get(0));
      name.setDeclarationTypes(declarations);
    }

    // 2. Check the object hierarchy
    List<FieldDeclaration> classFields = node.getAllBaseFields();
    for (FieldDeclaration field : classFields) {
      if (field.getIdentifier().equals(name.getLexeme())) {
        declarations.add(field);
      }
    }
    List<BaseMethodDeclaration> classMethods = node.getAllMethods();
    for (BaseMethodDeclaration method : classMethods) {
      if (method.getIdentifier().equals(name.getLexeme())) {
        declarations.add(method);
      }
    }
    if (!declarations.isEmpty()) {
      name.setDeclarationTypes(declarations);
    }

    // 3. Check single import
    if (unit.importDeclarations != null) {
      List<ImportDeclaration> importDeclarations = unit.importDeclarations.getAllImportsWithSuffix(name.getLexeme());
      if (!importDeclarations.isEmpty()) {
        String absolutePathToType = importDeclarations.get(0).getLexeme();
        Declaration declaration = (Declaration) symbolTable.findWithType(absolutePathToType, NameResolutionAlgorithm.CLASS_TYPES);
        if (declaration != null) {
          declarations.add(declaration);
          name.setDeclarationTypes(declarations);
        }
      }
    }

    // 4. Check the same package for a type declaration
    String packageNamePrefix = unit.packageDeclaration != null ? unit.packageDeclaration.getIdentifier() + "." : "";
    String packageClassName = packageNamePrefix + name.getLexeme();
    Token packageClassType = symbolTable.findWithType(packageClassName, NameResolutionAlgorithm.CLASS_TYPES);
    if (packageClassType != null) {
      declarations.add((Declaration)packageClassType);
      name.setDeclarationTypes(declarations);
    }

    // 5. Check on-demand import
    int matches = 0;
    if (unit.importDeclarations != null) {
      List<ImportDeclaration> onDemandImportDeclarations = unit.importDeclarations.getAllOnDemandImports();
      for (ImportDeclaration importDeclaration : onDemandImportDeclarations) {
        String absolutePathToType = importDeclaration.getLexeme() + '.' + name.getLexeme();
        Declaration declaration = (Declaration) symbolTable.findWithType(absolutePathToType, NameResolutionAlgorithm.CLASS_TYPES);
        if (declaration != null) {
          declarations.add(declaration);
          matches++;
        }
      }
    }

    if (matches > 1) throw new VariableNameResolutionException("Multiple on-demand imports found");
    else if (matches == 1) {
      name.setDeclarationTypes(declarations);
    }

    // 6. Try java.lang.* implicit on-demand package
    List<Token> javaLangDecls = symbolTable.find(NameResolutionAlgorithm.JAVA_LANG_PREFIX + name.getLexeme());
    for (Token type : javaLangDecls) {
      if (type instanceof ClassDeclaration || type instanceof InterfaceDeclaration) {
        declarations.add((Declaration) type);
      }
    }
    name.setDeclarationTypes(declarations);
  }

  private void resolveQualifiedNameDeclarations(CompilationUnit unit, Name name) throws VariableNameResolutionException {
    String[] identifiers = name.getLexeme().split("\\.");

    StringBuilder currentType = new StringBuilder();

    // 1.The initial base of a.b.c.d resolution. 'a' must resolve to a type.
    // 2. For the resolution of b.c we simply use the hierarchy to keep nesting fields
    // 3. Finally for d resolution we get all potential declaration(field, method) matches.
    currentType.append(identifiers[0]);
    Declaration lastMatchedDecl = resolveInitialQualified(unit, name, currentType, identifiers);
    if (lastMatchedDecl == null) {
      throw new VariableNameResolutionException("Nothing qualified for 0th of name: " + name.getLexeme(), name);
    }

    // 2.1. Check the object hierarchy, specifically for fields
    for (int i = 1; i < identifiers.length - 1; ++i) {
      boolean match = false;
      HierarchyGraphNode node = hierarchyGraph.get(currentType.toString());
      if (node != null) {
        List<FieldDeclaration> classFields = node.getAllFields();
        for (FieldDeclaration field : classFields) {
          if (field.getIdentifier().equals(identifiers[i])) {
            currentType.setLength(0);
            currentType.append(getTypePath(field.type));
            lastMatchedDecl = field;
            match = true;
            break;
          }
        }
      }
      if (!match) {
        currentType.append('.');
        currentType.append(identifiers[i]);
        Declaration classDecl = (Declaration) symbolTable.findWithType(
            currentType.toString(), NameResolutionAlgorithm.CLASS_TYPES);
        if (classDecl != null) continue;
        List<Token> pkgDecls = symbolTable.findWithPrefixOfAnyType(
            currentType.toString(), new Class[] {PackageDeclaration.class});
        if (pkgDecls == null) {
          throw new VariableNameResolutionException("Failed to disambiguate type: " + name.getLexeme(), name);
        }

      }
    }
    List<Declaration> declarations = new ArrayList<Declaration>();

    // Change edge case for array[] types.
    if (currentType.toString().contains("[]")) {
      if (!identifiers[identifiers.length - 1].equals("length")) {
        throw new VariableNameResolutionException("No Array[] field found", name);
      }
      declarations.add(lastMatchedDecl);
      name.setDeclarationTypes(declarations);
      return;
    }

    // Fill in all matching declarations.
    HierarchyGraphNode node = hierarchyGraph.get(currentType.toString());
    if (node != null) {
      List<BaseMethodDeclaration> classMethods = node.getAllMethods();
      for (BaseMethodDeclaration method : classMethods) {
        if (method.getIdentifier().equals(identifiers[identifiers.length - 1])) {
          declarations.add(method);

        }
      }
      List<FieldDeclaration> classFields = node.getAllFields();
      for (FieldDeclaration field : classFields) {
        if (field.getIdentifier().equals(identifiers[identifiers.length - 1])) {
          declarations.add(field);
        }
      }
    } else {
      currentType.append('.');
      currentType.append(identifiers[identifiers.length - 1]);
      Declaration decl = (Declaration) symbolTable.findWithType(
          currentType.toString(), NameResolutionAlgorithm.CLASS_TYPES);
      if (decl == null) {
        throw new VariableNameResolutionException(
            "Could not disambiguate the last suffix of name: " + name.getLexeme(), name);
      }
      declarations.add(decl);
    }
    name.setDeclarationTypes(declarations);
  }

  private Declaration resolveInitialQualified(CompilationUnit unit, Name name,
                                              StringBuilder currentType, String[] identifiers
  ) throws VariableNameResolutionException {
    // 1.1. Check variable table.
    Declaration lastMatchedDecl = null;
    List<Token> variableSymbols = variableTable.find(currentType.toString());
    if (!variableSymbols.isEmpty()) {
      lastMatchedDecl = (Declaration) variableSymbols.get(0);
      currentType.setLength(0);
      currentType.append(getTypePath(lastMatchedDecl.type));
      return lastMatchedDecl;
    }

    HierarchyGraphNode node = hierarchyGraph.get(identifiers[0]);
    if (node != null) {
      List<FieldDeclaration> classFields = node.getAllBaseFields();
      for (FieldDeclaration field : classFields) {
        if (field.getIdentifier().equals(name.getLexeme())) {
          lastMatchedDecl = field;
          currentType.setLength(0);
          currentType.append(getTypePath(lastMatchedDecl.type));
          return lastMatchedDecl;
        }
      }
      List<FieldDeclaration> selfFields = node.fields;
      for (FieldDeclaration field : selfFields) {
        if (field.getIdentifier().equals(identifiers[0])) throw new VariableNameResolutionException("Field name has not been initialized.");
      }
    }
    if (unit.typeDeclaration.getDeclaration().getIdentifier().equals(identifiers[0])) {
      lastMatchedDecl = unit.typeDeclaration.getDeclaration();
      currentType.setLength(0);
      currentType.append(lastMatchedDecl.getAbsolutePath());
      return lastMatchedDecl;
    }

    // 1.2. Check single import
    if (unit.importDeclarations != null) {
      List<ImportDeclaration> importDeclarations = unit.importDeclarations.getAllImportsWithSuffix(currentType.toString());
      if (!importDeclarations.isEmpty()) {
        String absolutePathToType = importDeclarations.get(0).getLexeme();
        lastMatchedDecl = (Declaration) symbolTable.findWithType(absolutePathToType, NameResolutionAlgorithm.CLASS_TYPES);
        if (lastMatchedDecl != null) {
          currentType.setLength(0);
          currentType.append(lastMatchedDecl.getAbsolutePath());
          return lastMatchedDecl;
        }
      }
    }

    // 1.3. Check package for Type, can only attempt to resolve when i = 0.
    String packageNamePrefix = unit.packageDeclaration != null ? unit.packageDeclaration.getIdentifier() + "." : "";
    String packageClassName = packageNamePrefix + identifiers[0];
    lastMatchedDecl = (Declaration) symbolTable.findWithType(packageClassName, NameResolutionAlgorithm.CLASS_TYPES);
    if (lastMatchedDecl != null) {
      currentType.setLength(0);
      currentType.append(lastMatchedDecl.getAbsolutePath());
      return lastMatchedDecl;
    }

    // 1.4. Check on-demand import
    int matches = 0;
    if (unit.importDeclarations != null) {
      List<ImportDeclaration> onDemandImportDeclarations = unit.importDeclarations.getAllOnDemandImports();
      for (ImportDeclaration importDeclaration : onDemandImportDeclarations) {
        String absolutePathToType = importDeclaration.getLexeme() + '.' + name.getLexeme();
        lastMatchedDecl = (Declaration) symbolTable.findWithType(absolutePathToType, NameResolutionAlgorithm.CLASS_TYPES);
        if (lastMatchedDecl != null) {
          currentType.setLength(0);
          currentType.append(lastMatchedDecl.getAbsolutePath());
          matches++;
        }
      }
    }
    if (matches > 1) {
      throw new VariableNameResolutionException("Ambiguous on demand import for name: " + name.getLexeme(), name);
    } else if (matches == 1) return lastMatchedDecl;

    // 1.5. Try java.lang.* implicit on-demand package
    List<Token> javaLangDecls = symbolTable.find(NameResolutionAlgorithm.JAVA_LANG_PREFIX + identifiers[0]);
    for (Token type : javaLangDecls) {
      if (type instanceof ClassDeclaration || type instanceof InterfaceDeclaration) {
        lastMatchedDecl = (Declaration) type;
        currentType.setLength(0);
        currentType.append(lastMatchedDecl.getAbsolutePath());
        return lastMatchedDecl;
      }
    }

    List<Token> pkgs = symbolTable.findWithPrefixOfAnyType(currentType.toString(), new Class[] {PackageDeclaration.class});
    if (pkgs != null && !pkgs.isEmpty()) lastMatchedDecl = (Declaration) pkgs.get(0);
    return lastMatchedDecl;
  }

  private String getTypePath(Type type) throws VariableNameResolutionException {
    if (type == null) {
      throw new VariableNameResolutionException("No type to reverse lookup", type);
    } else if (type.primitiveType != null) {
      throw new VariableNameResolutionException("Cannot get type path for primitive", type);
    } else if (type.referenceType.arrayType != null) {
      return "[]";
    } else {
      return type.referenceType.classOrInterfaceType.name.getAbsolutePath();
    }
  }

}
