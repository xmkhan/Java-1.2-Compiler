package visitor;

import exception.TypeLinkingVisitorException;
import exception.VisitorException;
import symbol.SymbolTable;
import token.ArrayType;
import token.ClassDeclaration;
import token.ClassOrInterfaceType;
import token.CompilationUnit;
import token.ImportDeclaration;
import token.ImportDeclarations;
import token.InterfaceDeclaration;
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

  // Ongoing data structures per CompilationUnit
  private HashMap<String, ImportDeclaration> imports;
  private TypeDeclaration typeDeclaration;
  private PackageDeclaration packageDeclaration;

  private static String javaLangPrefix = "java.lang.";

  public TypeLinkingVisitor(SymbolTable table) {
    this.table = table;
  }

  public void typeLink(List<CompilationUnit> units) throws VisitorException {
    for (CompilationUnit unit : units) {
      unit.acceptReverse(this);
    }
  }

  @Override
  public void visit(CompilationUnit token) throws VisitorException {
    super.visit(token);
    imports = new HashMap<String, ImportDeclaration>();
    typeDeclaration = token.typeDeclaration;
    packageDeclaration = token.packageDeclaration;

    HashSet<String> importSuffix = new HashSet<String>();
    if (token.importDeclarations != null) {
      List<ImportDeclaration> decls = token.importDeclarations.getImportDeclarations();
      for (ImportDeclaration decl : decls) {
        // Check for clash with ClassOrInterface name.
        if (decl.isSingle() && decl.containsSuffix(typeDeclaration.getDeclaration().getIdentifier())) {
          throw new TypeLinkingVisitorException("Import name clash with ClassName", token);
        }
        // Check to make sure on-demand package exists.
        if (decl.isOnDemand() && !table.containsAnyOfType(decl.getLexeme(), new Class[]{PackageDeclaration.class})) {
          throw new TypeLinkingVisitorException("No on-demand package found: " + decl.getLexeme(), token);
        }
        // Check for clashes between imports.
        if (importSuffix.contains(decl.getSuffix())) {
          throw new TypeLinkingVisitorException("Import name clash with Imports", token);
        }
        // Check to make sure import actually exists.
        if (!table.contains(decl.getLexeme())) {
          throw new TypeLinkingVisitorException("No known symbol for import: " + decl.getLexeme(), token);
        }
        importSuffix.add(decl.getSuffix());
        imports.put(decl.getSuffix(), decl);
      }
    }
  }

  @Override
  public void visit(Type token) throws VisitorException {
    super.visit(token);
    if (!(token.getType() instanceof ReferenceType)) return;

    Name name;
    if (((ReferenceType) token.getType()).getType() instanceof ClassOrInterfaceType) {
      ClassOrInterfaceType type = (ClassOrInterfaceType)((ReferenceType) token.getType()).getType();
      name = type.name;
    } else {
      ArrayType type = (ArrayType)((ReferenceType) token.getType()).getType();
      name = type.name;
    }

    // Check if the type exists in the SymbolTable w.r.t to the package.
    Class[] classTypes = new Class[] { ClassDeclaration.class, InterfaceDeclaration.class};
    if (name.isSimple() && !(
        table.containsAnyOfType(name.getLexeme(), classTypes) ||
        table.containsAnyOfType(packageDeclaration.getIdentifier() + "." + name.getLexeme(), classTypes) ||
        table.containsAnyOfType(javaLangPrefix + name.getLexeme(), classTypes))) {
      throw new TypeLinkingVisitorException("No class or interface for Type", token);
    }

  }
}
