package visitor;

import algorithm.name.resolution.NameResolutionAlgorithm;
import exception.NameResolutionException;
import exception.TypeLinkingVisitorException;
import exception.VisitorException;
import symbol.SymbolTable;
import token.ArrayType;
import token.ClassOrInterfaceType;
import token.CompilationUnit;
import token.ImportDeclaration;
import token.ImportDeclarations;
import token.Name;
import token.PackageDeclaration;
import token.ReferenceType;
import token.Type;
import token.TypeDeclaration;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * A visitor responsible for using a SymbolTable and checking for type linking errors.
 */
public class TypeLinkingVisitor extends BaseVisitor {
  private SymbolTable table;
  private NameResolutionAlgorithm algm;

  // Ongoing data structures per CompilationUnit
  private ImportDeclarations importDeclarations;
  private TypeDeclaration typeDeclaration;
  private PackageDeclaration packageDeclaration;

  public TypeLinkingVisitor(SymbolTable table) {
    this.table = table;
    this.algm = new NameResolutionAlgorithm(table);
  }

  public void typeLink(List<CompilationUnit> units) throws VisitorException {
    for (CompilationUnit unit : units) {
      unit.acceptReverse(this);
    }
  }

  @Override
  public void visit(CompilationUnit token) throws VisitorException {
    super.visit(token);
    typeDeclaration = token.typeDeclaration;
    packageDeclaration = token.packageDeclaration;
    importDeclarations = token.importDeclarations;

    if (packageDeclaration != null) {
      String[] packagePrefixes = token.packageDeclaration.getLexeme().split("\\.");
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < packagePrefixes.length; i++) {
        sb.append(packagePrefixes[i]);
        if (i > 0 && table.containsAnyOfType(sb.toString(), NameResolutionAlgorithm.CLASS_TYPES)) {
          throw new TypeLinkingVisitorException("Package name prefixes resolved to type", token);
        }
        sb.append('.');
      }
    }

    if (token.importDeclarations != null) {
      HashMap<String, ImportDeclaration> importSuffix = new HashMap<String, ImportDeclaration>();
      List<ImportDeclaration> decls = token.importDeclarations.getImportDeclarations();
      for (ImportDeclaration decl : decls) {
        // Check for clash with ClassOrInterface name.
        if (decl.isSingle() && decl.containsSuffix(typeDeclaration.getDeclaration().getIdentifier())) {
          // A class can import itself.
          String packageName = packageDeclaration != null ? packageDeclaration.getLexeme() + "." : "";
          String qualified = packageName + typeDeclaration.getDeclaration().getIdentifier();
          if (!qualified.equals(decl.getLexeme())) {
            throw new TypeLinkingVisitorException("Import name clash with ClassName", token);
          }
        }
        // Check to make sure on-demand package exists, or that it is a prefix of some package.
        if (decl.isOnDemand() && !table.containsAnyPrefixOfType(decl.getLexeme(), new Class[]{PackageDeclaration.class})) {
          throw new TypeLinkingVisitorException("No on-demand package found for: " + decl.getLexeme(), token);
        }
        // Check for clashes between imports.
        if (decl.isSingle() && importSuffix.containsKey(decl.getSuffix())) {
          if (!importSuffix.get(decl.getSuffix()).getLexeme().equals(decl.getLexeme())) {
            throw new TypeLinkingVisitorException("Import name clash with imports", token);
          }
        }
        // Check to make sure import actually exists.
        if (!table.containsPrefix(decl.getLexeme())) {
          throw new TypeLinkingVisitorException("No known symbol for import: " + decl.getLexeme(), token);
        }
        if (decl.isSingle()) importSuffix.put(decl.getSuffix(), decl);
      }
    }
  }

  @Override
  public void visit(ClassOrInterfaceType token) throws VisitorException {
    super.visit(token);
    if (!resolveName(token.name)) {
      throw new TypeLinkingVisitorException("Could not resolve Type", token);
    }
  }

  @Override
  public void visit(ArrayType token) throws VisitorException {
    super.visit(token);
    if (token.name != null && !resolveName(token.name)) {
      throw new TypeLinkingVisitorException("Could not resolve Type", token);
    }
  }

  private boolean resolveName(Name name) throws VisitorException {
    // Check if the type exists in the SymbolTable w.r.t to the package.
    try {
      return algm.resolveType(name, packageDeclaration, typeDeclaration, importDeclarations);
    } catch (NameResolutionException e) {
      return false;
    }
  }
}
