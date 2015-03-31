package visitor;

import exception.VisitorException;
import token.BaseMethodDeclaration;
import token.ClassBody;
import token.ClassBodyDeclaration;
import token.ClassDeclaration;
import token.CompilationUnit;
import token.Declaration;
import token.FieldDeclaration;
import token.InterfaceDeclaration;
import token.MethodDeclaration;
import token.Token;
import type.hierarchy.HierarchyGraph;
import type.hierarchy.HierarchyGraphNode;
import util.CodeGenUtils;

import java.util.HashMap;
import java.util.List;

/**
 * Responsible for performing final calculations
 */
public class PreliminaryCodeGenerationVisitor extends BaseVisitor {
  private HierarchyGraph graph;

  private boolean[][] isSubclassTable;

  public PreliminaryCodeGenerationVisitor(HierarchyGraph graph) {
    this.graph = graph;
  }

  public void setupClassMetadata(List<CompilationUnit> units) throws VisitorException {
    isSubclassTable = new boolean[units.size()][units.size()];

    for (int i = 0; i < units.size(); ++i) {
      CompilationUnit unit = units.get(i);
      if (unit.typeDeclaration.getDeclaration() instanceof ClassDeclaration) {
        ClassDeclaration classDeclaration = (ClassDeclaration) unit.typeDeclaration.getDeclaration();
        classDeclaration.classId = i;
      } else {
        InterfaceDeclaration interfaceDeclaration = (InterfaceDeclaration) unit.typeDeclaration.getDeclaration();
        interfaceDeclaration.classId = i;
      }
    }

    for (CompilationUnit unit : units) {
      unit.acceptReverse(this);
    }
  }

  public boolean[][] exportSubclassTable() {
    return isSubclassTable;
  }

  @Override
  public void visit(ClassDeclaration token) throws VisitorException {
    super.visit(token);
    ClassDeclaration classDeclaration = token;
    int classId = classDeclaration.classId;

    // Add 4 bytes of memory for the virtual table pointer.
    classDeclaration.classSize += 4;
    // Add 4 bytes for length of name.
    classDeclaration.vTableSize += 4;
    // Add the number of bytes based on length of name.
    classDeclaration.vTableSize += classDeclaration.getAbsolutePath().length();

    HierarchyGraphNode node = graph.get(classDeclaration.getAbsolutePath());
    // Create array of method labels.
    List<BaseMethodDeclaration> methods = node.getAllMethods();
    for (BaseMethodDeclaration method : methods) {
      if (method instanceof MethodDeclaration) {
        MethodDeclaration methodDeclaration = (MethodDeclaration) method;
        String methodLabel = CodeGenUtils.genMethodLabel(methodDeclaration);
        if (!classDeclaration.methodLabels.containsKey(methodLabel)) {
          classDeclaration.methodLabels.put(methodLabel, methodDeclaration);
        }
      }
    }

    // Add subclasses to the subclass table
    List<Token> baseClasses = node.getAllBaseClasses();
    for (int i = 1; i < baseClasses.size(); ++i) {
      if (baseClasses.get(i) instanceof ClassDeclaration) {
        ClassDeclaration classDecl = (ClassDeclaration) baseClasses.get(i);
        isSubclassTable[classId][classDecl.classId] = true;
      } else {
        InterfaceDeclaration interfaceDecl = (InterfaceDeclaration) baseClasses.get(i);
        isSubclassTable[classId][interfaceDecl.classId] = true;
      }
    }

    // Add 4 bytes per method to the vTable.
    classDeclaration.vTableSize += (4 * classDeclaration.methodLabels.size());

    // Create array of field labels.
    List<FieldDeclaration> fields = node.getAllFields();
    for (FieldDeclaration field : fields) {
      if (!classDeclaration.fieldLabels.containsKey(field.getIdentifier())) {
        classDeclaration.fieldLabels.put(field.getIdentifier(), field);
      }
    }
    // Add 4 bytes per field to the classSize.
    classDeclaration.classSize += (4 * classDeclaration.fieldLabels.size());
  }
}
