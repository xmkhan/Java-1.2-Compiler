package algorithm.name.resolution;

import exception.NameResolutionException;
import symbol.SymbolTable;
import token.ClassDeclaration;
import token.ImportDeclaration;
import token.ImportDeclarations;
import token.InterfaceDeclaration;
import token.Name;
import token.PackageDeclaration;
import token.Token;
import token.TypeDeclaration;

import java.util.List;

/**
 * Name resolution for chained types (a.b.c.d) according to the JLS.
 * Utility class used by the TypeLinkingVisitor
 */
public class NameResolutionAlgorithm {
  private SymbolTable table;

  public static Class[] CLASS_TYPES = new Class[] { ClassDeclaration.class, InterfaceDeclaration.class };
  public static String JAVA_LANG_PREFIX = "java.lang.";

  public NameResolutionAlgorithm(SymbolTable table) {
    this.table = table;
  }

  public boolean resolveType(Name name,
                             PackageDeclaration packageDeclaration,
                             TypeDeclaration typeDeclaration,
                             ImportDeclarations importDeclarations) throws NameResolutionException {
    if (name.isSimple()) {
      return resolveSimpleName(name, packageDeclaration, typeDeclaration, importDeclarations);
    } else {
      return resolveQualifiedName(name);
    }
  }

  public boolean resolveSimpleName(Name name,
                                   PackageDeclaration packageDeclaration,
                                   TypeDeclaration typeDeclaration,
                                   ImportDeclarations importDeclarations) throws NameResolutionException {

    // It's interesting to have a count of all ambiguous types for debug information later on.
    int matched = 0;
    // 1. Try the enclosing class or interface.
    if (name.getLexeme().equals(typeDeclaration.getDeclaration().getIdentifier())) {
      matched++;
    }
    // 2. Try any single-type import (A.B.C.D)
    if (importDeclarations != null && !importDeclarations.getAllImportsWithSuffix(name.getLexeme()).isEmpty())) {
      matched++;
    }
    // 3. Try the same package
    if (packageDeclaration != null &&
        table.containsAnyOfType(packageDeclaration.getIdentifier() + "." + name.getLexeme(), CLASS_TYPES)) {
      matched++;
    }
    // 4. Try any import-on-demand package (A.B.C.*), including java.lang.*
    if (importDeclarations != null) {
      List<ImportDeclaration> onDemandDecls = importDeclarations.getAllOnDemandImports();
      for (ImportDeclaration decl : onDemandDecls) {
        List<Token> types = table.findWithPrefixOfAnyType(decl.getLexeme(), CLASS_TYPES);
        for (Token type : types) {
          if (type.getLexeme().equals(name.getLexeme())) matched++;
        }
      }
    }

    if (matched > 1) {
      throw new NameResolutionException("Ambiguous type, multiple matches: " + name.getLexeme());
    }
    return true;
  }

  public boolean resolveQualifiedName(Name name) throws NameResolutionException {
    String[] identifiers = name.getLexeme().split("\\.");

    StringBuilder sb = new StringBuilder(name.getLexeme().length());
    // For all prefixes, make sure that there does not exist a type.
    for (int i = 0; i < identifiers.length - 1; ++i) {
      sb.append(identifiers[i]);
      if (table.containsAnyOfType(sb.toString(), CLASS_TYPES)) {
        throw new NameResolutionException("Prefix of Type resolved to a type: " + name.getLexeme());
      }
      if (!table.containsAnyPrefixOfType(sb.toString(), new Class[] {PackageDeclaration.class})) {
        throw new NameResolutionException("No package exists for Type: " + name.getLexeme());
      }
      sb.append('.');
    }
    // At the end, make sure that the final suffix resolves the entire decl to a type.
    sb.append(identifiers[identifiers.length - 1]);
    if (!table.containsAnyOfType(sb.toString(), CLASS_TYPES)) {
      throw new NameResolutionException("No type could be resolved for Type: " + name.getLexeme());
    }
    return true;
  }

}
