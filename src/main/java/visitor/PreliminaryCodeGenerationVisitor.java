package visitor;

import exception.VisitorException;
import token.AbstractMethodDeclaration;
import token.BaseMethodDeclaration;
import token.ClassDeclaration;
import token.CompilationUnit;
import token.FieldDeclaration;
import token.InterfaceDeclaration;
import token.InterfaceMemberDeclaration;
import token.MethodDeclaration;
import token.Token;
import type.hierarchy.HierarchyGraph;
import type.hierarchy.HierarchyGraphNode;
import util.CodeGenUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Responsible for performing final calculations
 */
public class PreliminaryCodeGenerationVisitor extends BaseVisitor {
  private final HierarchyGraph graph;
  private final int numInterfaceMethods;

  private boolean[][] subclassTable;

  public PreliminaryCodeGenerationVisitor(HierarchyGraph graph, int numInterfaceMethods) {
    this.graph = graph;
    this.numInterfaceMethods = numInterfaceMethods;
  }

  public void setupClassMetadata(List<CompilationUnit> units) throws VisitorException {
    subclassTable = new boolean[units.size()][units.size()];

    for (CompilationUnit unit : units) {
      unit.acceptReverse(this);
    }
  }

  public boolean[][] exportSubclassTable() {
    return subclassTable;
  }

  @Override
  public void visit(ClassDeclaration token) throws VisitorException {
    super.visit(token);
    ClassDeclaration classDeclaration = token;
    int classId = classDeclaration.classId;
    classDeclaration.classSize = 0;
    classDeclaration.vTableSize = 0;

    // Add 4 bytes of memory for the virtual table pointer.
    classDeclaration.classSize += 4;
    // Add the number of bytes based on length of name.
    classDeclaration.vTableSize += classDeclaration.getAbsolutePath().length() + 1;

    HierarchyGraphNode node = graph.get(classDeclaration.getAbsolutePath());
    // Create array of method labels.
    List<BaseMethodDeclaration> methods = node.getAllMethods();
    HashMap<String, MethodDeclaration> methodLabels = new HashMap<String, MethodDeclaration>();
    for (BaseMethodDeclaration method : methods) {
      if (method instanceof MethodDeclaration) {
        MethodDeclaration methodDeclaration = (MethodDeclaration) method;
        String methodLabel = CodeGenUtils.genMethodLabel(methodDeclaration);
        if (!methodLabel.contains(methodLabel)) {
          classDeclaration.methods.add(methodDeclaration);
          methodLabels.put(methodLabel, methodDeclaration);
        }
      }
    }
    // We want to be sorted from baseParent to derived
    Collections.reverse(classDeclaration.methods);

    for (int i = 0; i < classDeclaration.methods.size(); ++i) {
      classDeclaration.methods.get(i).methodId = i;
    }

    // Add subclasses to the subclass table
    List<Token> baseClasses = node.getAllBaseClasses();
    for (int i = 0; i < baseClasses.size(); ++i) {
      if (baseClasses.get(i) instanceof ClassDeclaration) {
        ClassDeclaration classDecl = (ClassDeclaration) baseClasses.get(i);
        subclassTable[classId][classDecl.classId] = true;
      } else {
        InterfaceDeclaration interfaceDecl = (InterfaceDeclaration) baseClasses.get(i);
        subclassTable[classId][interfaceDecl.classId] = true;
      }
    }

    // Add 4 bytes per method to the vTable.
    classDeclaration.vTableSize += (4 * classDeclaration.methods.size());

    // Create array of field labels.
    List<FieldDeclaration> fields = node.getAllFields();
    Set<String> fieldLabels = new HashSet<String>();
    for (FieldDeclaration field : fields) {
      if (!fieldLabels.contains(field.getIdentifier())) {
        classDeclaration.fields.add(field);
        fieldLabels.add(field.getIdentifier());
      }
    }
    // We want to be sorted from baseParent to derived.
    Collections.reverse(classDeclaration.fields);

    // Add # of bytes based on type for all fields for the class.
    for (FieldDeclaration field : classDeclaration.fields) {
      field.offset = classDeclaration.classSize;
      if (field.type.isPrimitiveType() && !field.type.isArray()) {
        classDeclaration.classSize += CodeGenUtils.getSize(field.type.getType().getLexeme());
      } else {
        classDeclaration.classSize += 4;
      }
    }

    // Create Class specific interface method dispatch table.
    List<Token> interfaces = node.getAllInterfaces();
    classDeclaration.interfaceMethods = new MethodDeclaration[numInterfaceMethods];
    for (Token interfaceToken : interfaces) {
      InterfaceDeclaration interfaceDeclaration = (InterfaceDeclaration) interfaceToken;
      if (interfaceDeclaration.interfaceBody.interfaceMemberDeclarations != null) {
        List<InterfaceMemberDeclaration> interfaceMemberDeclarations =
            interfaceDeclaration.interfaceBody.interfaceMemberDeclarations.getMemberDeclarations();
        for (InterfaceMemberDeclaration interfaceMemberDeclaration : interfaceMemberDeclarations) {
          int index = interfaceMemberDeclaration.abstractMethodDeclaration.interfaceMethodId;
          String methodLabel = CodeGenUtils.genMethodLabel(interfaceMemberDeclaration.abstractMethodDeclaration);
          if (!methodLabels.containsKey(methodLabel)) {
            throw new VisitorException(String.format("Interface Method label: %s in class: %s not found", methodLabel, classDeclaration.getIdentifier()), classDeclaration);
          }
          classDeclaration.interfaceMethods[index] = methodLabels.get(methodLabel);
        }
      }
    }
  }

}
