package visitor;

import exception.VisitorException;
import symbol.SymbolTable;
import token.*;
import type.hierarchy.HierarchyGraph;
import type.hierarchy.HierarchyGraphNode;
import util.CodeGenUtils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;

/**
 * Responsible for generating x86 assembly code for the program.
 */
public class CodeGenerationVisitor extends BaseVisitor {

  // Internal data members.
  private boolean[][] subclassTable;
  private final int numInterfaceMethods;
  private final SymbolTable table;
  private final HierarchyGraph graph;

  // Temporary data structures.
  private int numUnits;
  private int offset;
  private PrintStream output;
  private HashSet<String> importSet;
  private Stack<Stack<LocalVariableDeclaration>> declStack;
  private MethodDeclaration[][] selectorIndexTable;
  private MethodDeclaration testMainMethod;
  private int thisOffset;
  private ClassDeclaration objectDeclaration;

  public CodeGenerationVisitor(boolean[][] subclassTable, int numInterfaceMethods, SymbolTable table, HierarchyGraph graph) {
    this.subclassTable = subclassTable;
    this.numInterfaceMethods = numInterfaceMethods;
    this.table = table;
    this.graph = graph;
  }

  public void generateCode(List<CompilationUnit> units) throws FileNotFoundException, VisitorException {
    numUnits = units.size();
    selectorIndexTable = new MethodDeclaration[units.size()][numInterfaceMethods];
    for (CompilationUnit unit : units) {
      if (unit.typeDeclaration.getDeclaration() instanceof ClassDeclaration) {
        ClassDeclaration classDeclaration = (ClassDeclaration) unit.typeDeclaration.getDeclaration();
        for (int i = 0; i < classDeclaration.interfaceMethods.length; ++i) {
          // Nulls will automatically be initialized to null on assignment.
          selectorIndexTable[classDeclaration.classId][i] = classDeclaration.interfaceMethods[i];
        }
      }
    }

    // Initialize stack variables.
    offset = 0;
    declStack = new Stack<Stack<LocalVariableDeclaration>>();
    testMainMethod = null;

    // Find the Object declaration that is used by Arrays.
    for (CompilationUnit unit : units) {
      if (unit.typeDeclaration.getDeclaration().getAbsolutePath().equals("java.lang.Object")) {
        objectDeclaration = (ClassDeclaration) unit.typeDeclaration.getDeclaration();
      }
    }

    for (CompilationUnit unit : units) {
      output = new PrintStream(new FileOutputStream(String.format("output/%s.s",unit.typeDeclaration.getDeclaration().getIdentifier())));
      importSet = new HashSet<String>();
      unit.traverse(this);
      output.close();
    }
    output = new PrintStream(new FileOutputStream("output/__program.s"));
    output.println("section .text");
    output.println("extern __malloc");
    genSubtypeTable();
    genSelectorIndexTable();
    genPrimitiveArrayVTable();
    output.println("section .data");
    output.println("global _start");
    output.println("_start:");
    // Initialization code for all static methods.
    for (CompilationUnit unit : units) {
      if (unit.typeDeclaration.getDeclaration() instanceof ClassDeclaration) {
        ClassDeclaration classDeclaration = (ClassDeclaration) unit.typeDeclaration.getDeclaration();
        for (FieldDeclaration field : classDeclaration.fields) {
          if (field.containsModifier("static")) {
            field.traverse(this);
          }
        }
      }
    }
    output.println(String.format("extern %s", CodeGenUtils.genLabel(testMainMethod)));
    output.println(String.format("call %s", CodeGenUtils.genLabel(testMainMethod)));
    output.close();
  }

  private void genSubtypeTable() {
    output.println("; CODE GENERATION: genSubtypeTable");
    output.println("global __subtype_table");
    output.println("__subtype_table: dd 0");
    output.println(String.format("mov eax, %d",(4 * subclassTable.length)));
    output.println("call __malloc");
    output.println("mov [__subtype_table], eax");
    for(int i = 0; i < subclassTable.length; ++i) {
      output.println(String.format("mov eax, %d", (4 * subclassTable[i].length)));
      output.println("call __malloc");
      output.println(String.format("mov [__subtype_table + %d], eax", 4 * i));
      output.println(String.format("mov ebx, [__subtype_table + %d]", 4 * i));
      for (int j = 0; j < subclassTable[i].length; ++j) {
        output.println(String.format("mov dword [ebx + %d], %d", 4 * j, subclassTable[i][j] ? 1 : 0));
      }
    }
    output.println("; END genSubtypeTable");
  }

  private void genSelectorIndexTable() {
    output.println("; CODE GENERATION: genSelectorIndexTable");
    output.println("global __selector_index_table");
    output.println("__selector_index_table: dd 0");
    output.println(String.format("mov eax, %d",(4 * selectorIndexTable.length)));
    output.println("call __malloc");
    output.println("mov [__selector_index_table], eax");
    for(int i = 0; i < selectorIndexTable.length; ++i) {
      output.println(String.format("mov eax, %d", (4 * selectorIndexTable.length)));
      output.println("call __malloc");
      output.println(String.format("mov [__selector_index_table + %d], eax", 4 * i));
      output.println(String.format("mov ebx, [__selector_index_table + %d]", 4 * i));
      for (int j = 0; j < selectorIndexTable[i].length; ++j) {
        if (selectorIndexTable[i][j] == null) output.println(String.format("mov dword [ebx + %d], 0", 4 * j));
        else output.println(String.format("lea [ebx + %d], %s", 4 * j, CodeGenUtils.genLabel(selectorIndexTable[i][j])));
      }
    }
    output.println("; END genSelectorIndexTable");
  }

  private void genPrimitiveArrayVTable() {
    if (objectDeclaration != null) {
      output.println("; CODE GENERATION: genPrimitiveArrayVTable");
      String[] primitiveNames = new String[]{"boolean", "int", "char", "byte", "short"};
      for (int i = 0; i < primitiveNames.length; ++i) {
        String name = primitiveNames[i];
        output.println(String.format("global __vtable__%s_array", name));
        output.println(String.format("__vtable__%s_array: dd 0", name));
        output.println(String.format("mov eax, %d", objectDeclaration.vTableSize));
        output.println("call __malloc");
        output.println(String.format("mov [__vtable__%s_array], eax", name));
        output.println(String.format("mov dword [__vtable__%s_array], %d", name, numUnits + i));
        for (int j = 0; j < objectDeclaration.methods.size(); ++j) {
          output.println(String.format("lea [__vtable__%s_array + %d], %s", name, 4 * (j+1),
              CodeGenUtils.genLabel(objectDeclaration.methods.get(j))));
        }
      }
      output.println("; END genPrimitiveArrayVTable");
    }
  }

  private void genUniqueImport(Declaration declaration) {
    String label = CodeGenUtils.genLabel(declaration);
    if (!importSet.contains(label)) {
      output.println(String.format("extern %s", label));
      importSet.add(label);
    }
  }

  private void genUniqueImport(String label) {
    if (!importSet.contains(label)) {
      output.println(String.format("extern %s", label));
      importSet.add(label);
    }
  }

  @Override
  public void visit(ExpressionStatement token) throws VisitorException {
    super.visit(token);
    visitEveryChild(token);
  }

  @Override
  public void visit(FieldDeclaration token) throws VisitorException {
    super.visit(token);
    output.println("; CODE GENERATION: FieldDeclaration");
    int fieldSize = CodeGenUtils.getSize(token.type.getType().getLexeme());
    if (token.containsModifier("static")) {
      output.println(String.format("global %s", token.getAbsolutePath()));
      output.println(String.format("%s: %s 0", token.getAbsolutePath(), CodeGenUtils.getReserveSize(fieldSize)));
      if (token.expr != null) token.expr.traverse(this);
      else output.println("mov eax, 0");
      output.println(String.format("mov [%s], eax", token.getAbsolutePath()));
    } else {
      // For all non-static fields, we assume that the value at eax is 'this'.
      output.println("push eax");
      if (token.expr != null) token.expr.traverse(this);
      else output.println("mov eax, 0");
      output.println("mov ebx, eax");
      output.println("pop eax");
      output.println(String.format("mov [eax + %d], ebx", token.offset));
    }
    output.println("; END FieldDeclaration");
  }

  @Override
  public void visit(PackageDeclaration token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(InterfaceMemberDeclarations token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(Interfaces token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(ClassMemberDeclaration token) throws VisitorException {
    super.visit(token);
    if (token.children.get(0) instanceof FieldDeclaration) {
      FieldDeclaration field = (FieldDeclaration) token.children.get(0);
      if (field.containsModifier("static")) {
        field.traverse(this);
      }
    } else {
      token.children.get(0).traverse(this);
    }
  }

  @Override
  public void visit(WhileStatement token) throws VisitorException {
    super.visit(token);
    whileStatementHelper(token);
  }

  @Override
  public void visit(Modifier token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(IfThenStatement token) throws VisitorException {
    super.visit(token);
    String ifLabel = CodeGenUtils.genNextIfStatementLabel();

    visit(token.expression);

    output.println("cmp eax, 0");
    output.println("je " + ifLabel);

    visit(token.statement);

    output.println(String.format("%s:", ifLabel));
  }

  @Override
  public void visit(Modifiers token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(InterfaceDeclaration token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(SingleTypeImportDeclaration token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(ImportDeclaration token) throws VisitorException {
    super.visit(token);
    // General idea: We only fetch "Class" methods and static field labels. This excludes interfaces.
    List<Token> classWithPrefixes;
    if (token.isOnDemand()) {
      classWithPrefixes = table.findWithPrefixOfAnyType(token.getLexeme(), new Class[] {ClassDeclaration.class});
    } else {
      classWithPrefixes = table.find(token.getLexeme());
    }
    if (classWithPrefixes == null || classWithPrefixes.isEmpty()) {
      throw new VisitorException("Import Decl code gen, no class found for single import", token);
    }
    for (Token clazz : classWithPrefixes) {
      ClassDeclaration classDeclaration = (ClassDeclaration) clazz;
      for (MethodDeclaration method : classDeclaration.methods) {
        genUniqueImport(method);
      }
      for (FieldDeclaration field : classDeclaration.fields) {
        if (field.containsModifier("static")) {
          genUniqueImport(field);
        }
      }
    }
  }

  @Override
  public void visit(ClassBodyDeclaration token) throws VisitorException {
    super.visit(token);
    token.declaration.traverse(this);
  }

  @Override
  public void visit(InterfaceBody token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(ClassOrInterfaceType token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(IfThenElseStatementNoShortIf token) throws VisitorException {
    super.visit(token);
    ifThenElseVisitHelper(token);
  }

  @Override
  public void visit(QualifiedName token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(ClassBody token) throws VisitorException {
    super.visit(token);
    if (token.bodyDeclarations != null) token.bodyDeclarations.traverse(this);
  }

  @Override
  public void visit(ForStatement token) throws VisitorException {
    super.visit(token);
    forLoopVisitHelper(token);
  }

  @Override
  public void visit(TypeImportOnDemandDeclaration token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(AssignmentOperator token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(CharLiteral token) throws VisitorException {
    super.visit(token);
    // Handled in Literal
  }

  @Override
  public void visit(IntLiteral token) throws VisitorException {
    super.visit(token);
    // Handled in Literal
  }

  @Override
  public void visit(StringLiteral token) throws VisitorException {
    super.visit(token);
    // Handled in Literal
  }

  @Override
  public void visit(BooleanLiteral token) throws VisitorException {
    super.visit(token);
    // Handled in Literal
  }

  @Override
  public void visit(Literal token) throws VisitorException {
    super.visit(token);
    output.println("; CODE GENERATION: Literal");

    Token literal = token.getLiteral();

    if(token.isStringLiteral()) {
      constructString(literal.getLexeme());
    } else if(token.isCharLiteral()) {
      char value = token.getLexeme().charAt(0);
      output.println(String.format("mov eax, %d", (int) value));
    } else {
      int value = 0;
      switch (literal.getTokenType()) {
        case INT_LITERAL:
          value = Integer.parseInt(token.getLexeme());
          break;
        case BooleanLiteral:
          value = Boolean.parseBoolean(token.getLexeme()) ? 1 : 0;
          break;
        case NULL:
          value = 0;
          break;
      }

      output.println(String.format("mov eax, %d", value));
    }

    output.println("; END: Literal");
  }

  @Override
  public void visit(SimpleName token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(TypeDeclaration token) throws VisitorException {
    super.visit(token);
    if (token.classDeclaration != null) token.classDeclaration.traverse(this);
  }

  @Override
  public void visit(Expression token) throws VisitorException {
    super.visit(token);
    output.println("; CODE GENERATION: Expression");
    token.children.get(0).traverse(this);
    output.println("; END: Expression");
  }

  @Override
  public void visit(MultiplicativeExpression token) throws VisitorException {
    super.visit(token);
    if(token.isDefined()) {
      output.println("; CODE GENERATION: MultiplicativeExpression");
      token.expr1.traverse(this);
      output.println("push eax");
      token.expr.traverse(this);
      output.println("mov ebx, eax");
      output.println("pop eax");
      if(token.operator.getTokenType().equals(TokenType.MULT_OP)) {
        output.println("imul eax, ebx");
      } else {
        String startLabel = CodeGenUtils.genNextTempLabel();
        output.println("cmp ebx, 0");
        output.println(String.format("jne %s", startLabel));
        output.println("call __exception");
        output.println(startLabel + ":");
        output.println("cdq");
        output.println("idiv ebx");

        if(token.operator.getTokenType().equals(TokenType.MOD_OP)) {
          output.println("mov eax, edx");
        }
      }
      output.println("; END: MultiplicativeExpression");
    } else {
      token.expr.traverse(this);
    }
  }

  @Override
  public void visit(AdditiveExpression token) throws VisitorException {
    super.visit(token);
    if(token.isDefined()) {
      output.println("; CODE GENERATION: AdditiveExpression");
      token.leftExpr.traverse(this);
      output.println("push eax");
      token.rightExpr.traverse(this);
      output.println("pop ebx");
      if(token.isAdd()) {
        if(token.isLeftString() || token.isRightString()) {
          output.println("mov ecx, eax");
          // result of concat will be in eax
          concat("ebx", "ecx", token);
        } else {
          output.println("add eax, ebx");
        }
      } else {
        output.println("sub ebx, eax");
        output.println("mov eax, ebx");
      }
      output.println("; END: AdditiveExpression");
    } else {
      token.rightExpr.traverse(this);
    }
  }

  @Override
  public void visit(RelationalExpression token) throws VisitorException {
    super.visit(token);
    if(token.isDefined()) {
      output.println("; CODE GENERATION: RelationalExpression");
      token.leftExpr.traverse(this);

      if(token.getOperator().getTokenType().equals(TokenType.INSTANCEOF)) {
        output.println("mov ecx, eax");
        output.println("mov eax, 0");

        int classId;
        if(token.referenceType.isReferenceType()) {
          Declaration decl = token.referenceType.getReferenceName().getDeterminedDeclaration();
          if(decl instanceof ClassDeclaration) {
            classId = ((ClassDeclaration) decl).classId;
          } else {
            classId = ((InterfaceDeclaration) decl).classId;
          }

          if(token.referenceType.isArray()) {
            classId += numUnits;
          }
        } else {
          classId = CodeGenUtils.getArrayPrimitiveClassId(numUnits, token.referenceType.getType().getLexeme());
        }

        String endLabel = CodeGenUtils.genNextTempLabel();
        output.println("cmp ecx, 0");
        output.println(String.format("je %s", endLabel));
        output.println("mov ecx, [ecx]");
        output.println("mov ecx, [ecx]");
        output.println("mul ecx, 4");
        output.println("add ecx, __subtype_table");
        output.println("mov ecx, [ecx]");
        output.println(String.format("mov ecx, [ecx + %d]", classId * 4));
        output.println("cmp ecx, 0");
        output.println(String.format("je %s", endLabel));
        output.println("mov eax, 1");
        output.println(endLabel + ":");
      } else {
        output.println("push eax");
        token.rightExpr.traverse(this);
        output.println("pop ebx");
        output.println("mov ecx, eax");
        output.println("mov eax, 0");

        String endLabel = CodeGenUtils.genNextTempLabel();
        output.println("cmp ebx, ecx");
        switch (token.getOperator().getTokenType()) {
          case LESS_THAN_OP_EQUAL:
            output.println(String.format("jg %s", endLabel));
            break;
          case LESS_THAN_OP:
            output.println(String.format("jge %s", endLabel));
            break;
          case MORE_THAN_OP:
            output.println(String.format("jle %s", endLabel));
            break;
          case MORE_THAN_OP_EQUAL:
            output.println(String.format("jl %s", endLabel));
            break;
        }
        output.println("mov eax, 1");
        output.println(endLabel + ":");
      }
      output.println("; END: RelationalExpression");
    } else {
      token.rightExpr.traverse(this);
    }
  }

  @Override
  public void visit(EqualityExpression token) throws VisitorException {
    super.visit(token);
    if(token.isDefined()) {
      String endLabel = CodeGenUtils.genNextTempLabel();

      output.println("; CODE GENERATION: EqualityExpression");
      token.leftExpr.traverse(this);
      output.println("push eax");
      token.rightExpr.traverse(this);
      output.println("pop ebx");
      output.println("mov ecx, eax");
      output.println("mov eax, 0");
      output.println("cmp ebx, ecx");
      if (token.isEqualCheck()) {
        output.println(String.format("jne %s", endLabel));
      } else {
        output.println(String.format("je %s", endLabel));
      }
      output.println("mov eax, 1");
      output.println(endLabel + ":");
      output.println("; END: EqualityExpression");
    } else {
      token.rightExpr.traverse(this);
    }
  }

  @Override
  public void visit(AndExpression token) throws VisitorException {
    super.visit(token);
    if(token.isDefined()) {
//      String endLabel = CodeGenUtils.genNextTempLabel();

      output.println("; CODE GENERATION: AndExpression");
      token.leftExpr.traverse(this);
      output.println("push eax");
      token.rightExpr.traverse(this);
      output.println("pop ebx");
      output.println("and eax ebx");
//      output.println("add ebx, eax");
//      output.println("mov eax, 0");
//      output.println("cmp ebx, 2");
//      output.println(String.format("jne %s", endLabel));
//      output.println("mov eax, 1");
//      output.println(endLabel + ":");
      output.println("; END: AndExpression");
    } else {
      token.rightExpr.traverse(this);
    }
  }

  @Override
  public void visit(InclusiveOrExpression token) throws VisitorException {
    super.visit(token);
    if (token.isDefined()) {
//      String endLabel = CodeGenUtils.genNextTempLabel();

      output.println("; CODE GENERATION: InclusiveOrExpression");
      token.leftExpr.traverse(this);
      output.println("push eax");
      token.rightExpr.traverse(this);
      output.println("pop ebx");
      output.println("or eax, ebx");
//      output.println("add eax, ebx");
//      output.println("cmp eax, 2");
//      output.println(String.format("jne %s", endLabel));
//      output.println("mov eax, 1");
//      output.println(endLabel + ":");
      output.println("; END: InclusiveOrExpression");
    } else {
      token.rightExpr.traverse(this);
    }
  }

  @Override
  public void visit(ConditionalOrExpression token) throws VisitorException {
    super.visit(token);
    if(token.isDefined()) {
      String endLabel = CodeGenUtils.genNextTempLabel();

      output.println("; CODE GENERATION: ConditionalOrExpression");
      token.leftExpr.traverse(this);
      output.println("cmp eax, 1");
      output.println(String.format("je %s", endLabel));
      token.rightExpr.traverse(this);
      output.println(endLabel + ":");
      output.println("; END: ConditionalOrExpression");
    } else {
      token.rightExpr.traverse(this);
    }
  }

  @Override
  public void visit(ConditionalAndExpression token) throws VisitorException {
    super.visit(token);
    if(token.isDefined()) {
      String endLabel = CodeGenUtils.genNextTempLabel();

      output.println("; CODE GENERATION: ConditionalAndExpression");
      token.leftExpr.traverse(this);
      output.println("cmp eax, 0");
      output.println(String.format("je %s", endLabel));
      token.rightExpr.traverse(this);
      output.println(endLabel + ":");
      output.println("; END: ConditionalAndExpression");
    } else {
      token.rightExpr.traverse(this);
    }
  }

  @Override
  public void visit(Primary token) throws VisitorException {
    super.visit(token);
    if(token.children.size() == 1) {
      if(token.children.get(0).getTokenType() == TokenType.THIS) {
        output.println("; CODE GENERATION: Primary");
        output.println(String.format("mov eax, [ebp + %d]", thisOffset));
        output.println("; END: Primary");
      } else {
        token.children.get(0).traverse(this);
      }
    } else {
     token.children.get(1).traverse(this);
    }
  }

  @Override
  public void visit(FieldAccess token) throws VisitorException {
    super.visit(token);
    output.println("; CODE GENERATION: FieldAccess");
    token.primary.traverse(this);
    checkForNull("eax");

    if(token.primary.getDeterminedType().isArray) {
      output.println("mov eax, [eax + 4]");
    } else {
      FieldDeclaration decl = (FieldDeclaration) token.getDeterminedDeclaration();
      output.println(String.format("mov eax, [eax + %d]", decl.offset));
    }
    output.println("; END: FieldAccess");
  }

  @Override
  public void visit(ArrayAccess token) throws VisitorException {
    super.visit(token);
    output.println("; CODE GENERATION: ArrayAccess");
    if(token.isAccessOnPrimary()) {
      token.primary.traverse(this);
    } else {
      generateNameAccess(token.name, false);
    }

    checkForNull("eax");
    String label = CodeGenUtils.genNextTempLabel();
    output.println("push eax");
    token.expression.traverse(this);
    output.println("pop ebx");
    output.println("cmp eax, [ebx + 4]");
    output.println(String.format("jl %s", label));
    output.println("call __exception");
    output.println(label + ":");
    output.println("mov eax, [ebx + eax * 4 + 8]");
    output.println("; END: ArrayAccess");
  }

  @Override
  public void visit(MethodInvocation token) throws VisitorException {
    super.visit(token);
    output.println("; CODE GENERATION: MethodInvocation");
    CodeGenUtils.genPushRegisters(output, true);
    if(token.isOnPrimary()) {
      token.primary.traverse(this);
      checkForNull("eax");
      output.println("push eax");

      TypeCheckToken primaryType = token.primary.getDeterminedType();
      if(primaryType.declaration instanceof InterfaceDeclaration) {
        AbstractMethodDeclaration methodDecl = (AbstractMethodDeclaration) token.getDeterminedDeclaration();
        output.println("mov eax, [eax]");
        output.println("mov eax, [eax]");
        output.println("mul eax, 4");
        output.println("mov eax, [__selector_index_table + eax]");
        output.println(String.format("mov eax, [eax + %d]", methodDecl.interfaceMethodId * 4));
      } else {
        MethodDeclaration methodDecl = (MethodDeclaration) token.getDeterminedDeclaration();
        output.println("mov eax, [eax]");
        output.println(String.format("mov eax, [eax + %d]", methodDecl.methodId * 4 + 4));
      }
    } else {
      generateNameAccess(token.name, true);
    }

    // eax now points to the method to call and this is on stack set to value if non-static call
    // this on stack for static but is null
    output.println("mov ebx, eax");

    // push arguments - for native assume even though pushed eax still has value
    if(token.hasArguments()) {
      for (Expression expression : token.argumentList.getArgumentList()) {
        output.println("push ebx");
        expression.traverse(this);
        output.println("pop ebx");
        output.println("push eax");
      }
    }

    output.println("call ebx");

    // pop off arguments
    if(token.hasArguments()) {
      for (int a = 0; a < token.argumentList.numArguments(); a++) {
        output.println("pop ebx");
      }
    }
    // pop this
    output.println("pop ebx");

    CodeGenUtils.genPopRegisters(output, true);
    // eax should have return value
    output.println("; END: MethodInvocation");
  }

  @Override
  public void visit(LeftHandSide token) throws VisitorException {
    super.visit(token);
    output.println("; CODE GENERATION: LeftHandSide");
    Token child = token.children.get(0);
    if(child instanceof Name) {
      generateNameAccessForReference((Name) child);
    } else if(child instanceof FieldAccess) {
      FieldAccess fieldAccess = (FieldAccess) child;
      fieldAccess.primary.traverse(this);
      checkForNull("eax");

      if(fieldAccess.primary.getDeterminedType().isArray) {
        // TODO: catch this earlier
      } else {
        FieldDeclaration decl = (FieldDeclaration) fieldAccess.getDeterminedDeclaration();
        output.println(String.format("lea eax, [eax + %d]", decl.offset));
      }
    } else if(child instanceof ArrayAccess) {
      ArrayAccess arrayAccess = (ArrayAccess) child;

      if(arrayAccess.isAccessOnPrimary()) {
        arrayAccess.primary.traverse(this);
      } else {
        generateNameAccess(arrayAccess.name, false);
      }

      checkForNull("eax");
      String label = CodeGenUtils.genNextTempLabel();
      output.println("push eax");
      arrayAccess.expression.traverse(this);
      output.println("pop ebx");
      output.println("cmp eax, [ebx + 4]");
      output.println(String.format("jl %s", label));
      output.println("call __exception");
      output.println(label + ":");
      output.println("lea eax, [ebx + eax * 4 + 8]");
    }
    output.println("; END: LeftHandSide");
  }

  @Override
  public void visit(AssignmentExpression token) throws VisitorException {
    super.visit(token);
    token.children.get(0).traverse(this);
  }

  @Override
  public void visit(Assignment token) throws VisitorException {
    super.visit(token);
    output.println("; CODE GENERATION: Assignment");
    token.leftHandSide.traverse(this);
    output.println("push eax");
    token.assignmentExpression.traverse(this);
    output.println("pop ebx");
    output.println("mov [ebx], eax");
    output.println("; END: Assignment");
  }

  @Override
  public void visit(ArrayCreationExpression token) throws VisitorException {
    super.visit(token);
    // Determine the label for the vtable.
    String vTableName;
    if (token.name != null) {
      vTableName = token.name.getDeterminedDeclaration().getAbsolutePath();
    } else {
      vTableName = token.primitiveType.getType().getLexeme();
    }

    output.println("; CODE GENERATION: ArrayCreationExpression");
    CodeGenUtils.genPushRegisters(output, true);
    token.expression.traverse(this);
    output.println("mov ebx, eax");
    output.println("lea ebx, [ebx + 2]");
    // Expression should have returned an integer for the size, we add 8 for the vtable_ptr and length.
    output.println("lea eax, [eax * 4 + 8]");
    output.println("call __malloc");
    // Initialize the array to default values.
    String begin = CodeGenUtils.genUniqueLabel();
    String end = CodeGenUtils.genUniqueLabel();
    output.println("mov ecx, 0");
    output.println(String.format("%s:", begin));
    output.println("cmp ecx, ebx");
    output.println(String.format("jge %s", end));
    output.println("mov dword [eax + ecx * 4], 0");
    output.println("inc ecx");
    output.println(String.format("jmp %s", begin));
    output.println(String.format("%s:", end));
    // Move the address of the vtable as 0th index.
    output.println(String.format("mov dword [eax], __vtable__%s_array", vTableName));
    // Move length as 1st index.
    output.println("lea ebx, [ebx - 2]");
    output.println("mov [eax + 4], ebx");
    CodeGenUtils.genPopRegisters(output, true);
    output.println("; END ArrayCreationExpression");
  }

  @Override
  public void visit(CastExpression token) throws VisitorException {
    super.visit(token);
    output.println("; CODE GENERATION: CastExpression");
    if (token.isArrayCast()) {
      token.children.get(5).traverse(this);
    } else {
      token.children.get(3).traverse(this);
    }

    if (token.isArrayCast() || token.isName()) {
      int classId;
      if (token.isName()) {
        Declaration decl = token.name.getDeterminedDeclaration();
        if (decl instanceof ClassDeclaration) {
          classId = ((ClassDeclaration) decl).classId;
        } else {
          classId = ((InterfaceDeclaration) decl).classId;
        }

        if (token.isArrayCast()) {
          classId += numUnits;
        }
      } else {
        classId = CodeGenUtils.getArrayPrimitiveClassId(numUnits, token.primitiveType.getType().getLexeme());
      }

      String endLabel = CodeGenUtils.genNextTempLabel();
      output.println("cmp eax, 0");
      output.println(String.format("je %s", endLabel));
      output.println("mov ebx, [eax]");
      output.println("mov ebx, [ebx]");
      output.println("mul ebx, 4");
      output.println("add ebx, __subtype_table");
      output.println("mov ebx, [ebx]");
      output.println(String.format("mov ebx, [ebx + %d]", classId * 4));
      output.println("cmp ebx, 1");
      output.println(String.format("je %s", endLabel));
      output.println("call __exception");
      output.println(endLabel + ":");
    } else {
      int size = CodeGenUtils.getActualSize(token.primitiveType.getType().getLexeme());
      switch (size) {
        case 2:
          output.println("movsx eax, ax");
          break;
        case 1:
          output.println("movsx eax, al");
          break;
      }
    }
    output.println("; END: CastExpression");
  }

  @Override
  public void visit(IfThenElseStatement token) throws VisitorException {
    super.visit(token);
    ifThenElseVisitHelper(token);
  }

  @Override
  public void visit(UnaryExpression token) throws VisitorException {
    super.visit(token);
    if(token.isNegative()) {
      output.println("; UnaryExpression");
      token.exp.traverse(this);
      output.println("neg eax");
      output.println("; END: UnaryExpression");
    } else {
      token.posExp.traverse(this);
    }
  }

  @Override
  public void visit(UnaryExpressionNotMinus token) throws VisitorException {
    super.visit(token);
    if(token.isNeg()) {
      output.println("; UnaryExpressionNotMinus");
      token.unaryExpression.traverse(this);
      output.println("not eax");
      output.println("; END: UnaryExpressionNotMinus");
    } else if(token.name != null) {
      output.println("; UnaryExpressionNotMinus");
      generateNameAccess(token.name, false);
      output.println("; END: UnaryExpressionNotMinus");
    } else if(token.primary != null) {
      token.primary.traverse(this);
    } else if(token.castExpression != null) {
      token.castExpression.traverse(this);
    }
  }

  @Override
  public void visit(ConstructorDeclaration token) throws VisitorException {
    super.visit(token);
    output.println("; CODE GENERATION: ConstructorDeclaration");
    String label = CodeGenUtils.genLabel(token);
    output.println(String.format("global %s", label));
    output.println(String.format("%s:", label));
    ClassDeclaration classDeclaration = (ClassDeclaration) table.getClass(token);
    HierarchyGraphNode node = graph.get(classDeclaration.getAbsolutePath());
    List<Token> classTokens = node.getAllBaseClasses();
    if (!node.extendsList.isEmpty()) {
      // Call the default constructor for the base class.
      ClassDeclaration baseClass = (ClassDeclaration) node.extendsList.get(0).classOrInterface;
      String baseLabel = String.format("%s.%s#void", baseClass.getAbsolutePath(), baseClass.getIdentifier());
      if (!importSet.contains(baseLabel)) {
        output.println(String.format("extern %s", baseLabel));
        importSet.add(baseLabel);
      }
      output.println(String.format("call %s", baseLabel));
    }

    int paramOffset = 8; // Accounts for [ebp, eip] on stack.
    // Setup the offsets for the function parameter offsets.
    List<FormalParameter> parameters = token.getParameters();
    for (int i = parameters.size() - 1; i >= 0 ; --i) {
      FormalParameter param = parameters.get(i);
      param.offset = paramOffset;
      paramOffset += CodeGenUtils.getSize(param.getType().getLexeme());
    }
    // Initialize the non-static fields. Firstly, we put 'this' into eax.
    output.println(String.format("mov eax, [ebp + %d]", paramOffset));
    thisOffset = paramOffset;
    for (FieldDeclaration field : classDeclaration.fields) {
      if (!field.containsModifier("static")) {
        field.traverse(this);
      }
    }
    // Set the vtpr and classId
    output.println(String.format("mov eax, __vtable__%s", classDeclaration.getAbsolutePath()));
    output.println(String.format("mov dword [eax], %d", classDeclaration.classId));
    offset = 0;
    token.newScope.traverse(this);
    token.body.traverse(this);
    token.closeScope.traverse(this);
    output.println("mov esp, ebp");
    output.println("pop ebp");
    output.println("ret");
    output.println("; END ConstructorDeclaration");
  }

  @Override
  public void visit(LocalVariableDeclaration token) throws VisitorException {
    super.visit(token);
    output.println("; CODE GENERATION: LocalVariableDeclaration");
    token.offset = offset;
    token.expression.traverse(this);
    output.println("push eax");
    offset += CodeGenUtils.getSize(token.type.getType().getLexeme());
    output.println("; END LocalVariableDeclaration");
  }

  @Override
  public void visit(InterfaceTypeList token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(ReturnStatement token) throws VisitorException {
    super.visit(token);
    addComment("rtn statement " + token.getLexeme());
    // Executed the return expression
    if (token.children.size() == 3) token.children.get(1).traverse(this);
    output.println("mov esp, ebp");
    output.println("pop ebp");
    output.println("ret");
  }

  @Override
  public void visit(StatementNoShortIf token) throws VisitorException {
    super.visit(token);
    addComment("StatementNoShortIf " + token.getLexeme());
    visitEveryChild(token);
  }

  @Override
  public void visit(BlockStatement token) throws VisitorException {
    super.visit(token);
    addComment("BlockStatement " + token.getLexeme());
    visitEveryChild(token);
  }

  @Override
  public void visit(MethodDeclarator token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(ClassInstanceCreationExpression token) throws VisitorException {
    super.visit(token);
    output.println("; CODE GENERATION: ClassInstanceCreationExpression");
    if (!(token.classType.classOrInterfaceType.name.getDeterminedDeclaration() instanceof ConstructorDeclaration)) {
      throw new VisitorException(String.format("Incorrect determined declaration for %s, expected ConstructorDeclaration",
          token.classType.classOrInterfaceType.name.getLexeme()), token);
    }
    ConstructorDeclaration constructorDeclaration = (ConstructorDeclaration) token.classType.classOrInterfaceType.name.getDeterminedDeclaration();
    ClassDeclaration classDeclaration =  (ClassDeclaration) table.getClass(constructorDeclaration);
    CodeGenUtils.genPushRegisters(output, true);
    output.println(String.format("mov eax, %d", classDeclaration.classSize));
    output.println("call __malloc");
    // Push "this" on the stack.
    output.println("push eax");
    // Push on arguments.
    if (token.argumentList != null && !token.argumentList.argumentList.isEmpty()) {
      for (Expression expr : token.argumentList.argumentList) {
        expr.traverse(this);
        output.println("push eax");
      }
    }
    output.println(String.format("call %s", CodeGenUtils.genLabel(constructorDeclaration)));
    // Pop off arguments.
    if (token.argumentList != null && !token.argumentList.argumentList.isEmpty()) {
      for (Expression expr : token.argumentList.argumentList) {
        output.println("pop eax");
      }
    }
    // Pop "this" from the stack
    output.println("pop eax");
    CodeGenUtils.genPopRegisters(output, true);
    output.println("; END ClassInstanceCreationExpression");
  }

  @Override
  public void visit(MethodDeclaration token) throws VisitorException {
    super.visit(token);
    output.println("; CODE GENERATION: MethodDeclaration");
    // Keep track of test method to generate starting point.
    if (testMainMethod == null && isTestMethod(token)) testMainMethod = token;

    output.println(String.format("global %s", CodeGenUtils.genLabel(token)));
    output.println(String.format("%s:", CodeGenUtils.genLabel(token)));
    int paramOffset = 8; // Accounts for [ebp, eip] on stack.
    // Setup the offsets for the function parameter offsets.
    List<FormalParameter> parameters = token.getParameters();
    for (int i = parameters.size() - 1; i >= 0 ; --i) {
      FormalParameter param = parameters.get(i);
      param.offset = paramOffset;
      paramOffset += CodeGenUtils.getSize(param.getType().getLexeme());
    }
    thisOffset = !token.methodHeader.modifiers.containsModifier("static") ? paramOffset : 0;

    offset = 0;
    token.newScope.traverse(this);
    token.methodBody.traverse(this);
    token.closeScope.traverse(this);
    if (token.methodHeader.isVoid()) {
      output.println("mov esp, ebp");
      output.println("pop ebp");
      output.println("ret");
    }
    output.println("; END MethodDeclaration");
  }

  private void generateNameAccess(Name name, boolean pushThisOnStackForMethod) {
    output.println("; CODE GENERATION: generateNameAccess");
    boolean thisKeptTrack = false;
    List<Declaration> declarationPaths = name.getDeclarationPath();
    if(declarationPaths == null) declarationPaths = new ArrayList<Declaration>();
    declarationPaths.add(name.getDeterminedDeclaration());
    int startIdx = 0;
    for (; startIdx < declarationPaths.size(); startIdx++) {
      Declaration curr = declarationPaths.get(startIdx);
      if(curr instanceof LocalVariableDeclaration) {
        LocalVariableDeclaration localVariableDeclaration = (LocalVariableDeclaration) curr;
        output.println(String.format("mov eax, [ebp - %d]", localVariableDeclaration.offset));
        break;
      } else if(curr instanceof FormalParameter) {
        FormalParameter formalParameter = (FormalParameter) curr;
        output.println(String.format("mov eax, [ebp + %d]", formalParameter.offset));
        break;
      } else if(curr instanceof FieldDeclaration) {
        FieldDeclaration fieldDeclaration = (FieldDeclaration) curr;
        if(fieldDeclaration.containsModifier("static")) {
          genUniqueImport(fieldDeclaration);
          output.println(String.format("mov eax, [%s]", CodeGenUtils.genLabel(fieldDeclaration)));
        } else {
          output.println(String.format("mov eax, [ebp + %d]", thisOffset));
          output.println(String.format("mov eax, [eax + %d]", fieldDeclaration.offset));
        }
        break;
      } else if (curr instanceof MethodDeclaration) {
        MethodDeclaration methodDeclaration = (MethodDeclaration) curr;
        if(methodDeclaration.methodHeader.containsModifier("static")) {
          genUniqueImport(methodDeclaration);
          output.println(String.format("mov eax, %s", CodeGenUtils.genLabel(methodDeclaration)));
        } else {
          output.println(String.format("mov eax, [ebp + %d]", thisOffset));
          if(pushThisOnStackForMethod) {
            output.println("mov ecx, eax");
            thisKeptTrack = true;
          }
          output.println("mov eax, [eax]");
          output.println(String.format("mov eax, [eax + %d]", methodDeclaration.methodId * 4 + 4));
        }
      } else if (curr instanceof AbstractMethodDeclaration) {
        AbstractMethodDeclaration methodDeclaration = (AbstractMethodDeclaration) curr;
        output.println(String.format("mov eax, [ebp + %d]", thisOffset));
        if(pushThisOnStackForMethod) {
          output.println("mov ecx, eax");
          thisKeptTrack = true;
        }
        output.println("mov eax, [eax]");
        output.println("mov eax, [eax]");
        output.println("mul eax, 4");
        output.println("mov eax, [__selector_index_table + eax]");
        output.println(String.format("mov eax, [eax + %d]", methodDeclaration.interfaceMethodId * 4));
      }
    }

    for(startIdx = startIdx + 1; startIdx < declarationPaths.size(); startIdx++) {
      // Safe to assume that no static accesses here
      checkForNull("eax");

      Declaration curr = declarationPaths.get(startIdx);

      // Special case for array.length
      if(curr == null && startIdx == declarationPaths.size() - 1 && name.getLexeme().endsWith(".length")) {
        output.println("mov eax, [eax + 4]");
        continue;
      }

      if(curr instanceof FieldDeclaration) {
        output.println(String.format("mov eax, [eax + %d]", ((FieldDeclaration) curr).offset));
      } else if(curr instanceof MethodDeclaration) {
        if(pushThisOnStackForMethod) {
          output.println("mov ecx, eax");
          thisKeptTrack = true;
        }
        output.println("mov eax, [eax]");
        output.println(String.format("mov eax, [eax + %d]", ((MethodDeclaration) curr).methodId * 4 + 4));
      } else if(curr instanceof AbstractMethodDeclaration) {
        if(pushThisOnStackForMethod) {
          output.println("mov ecx, eax");
          thisKeptTrack = true;
        }
        output.println("mov eax, [eax]");
        output.println("mov eax, [eax]");
        output.println("mul eax, 4");
        output.println("mov eax, [__selector_index_table + eax]");
        output.println(String.format("mov eax, [eax + %d]", ((AbstractMethodDeclaration) curr).interfaceMethodId * 4));
      }
    }

    if(pushThisOnStackForMethod) {
      if (thisKeptTrack) {
        output.println("push ecx");
      } else {
        output.println("push 0");
      }
    }
    output.println("; END: generateNameAccess");
  }

  private void generateNameAccessForReference(Name name) {
    output.println("; CODE GENERATION: generateNameAccessForReference");
    List<Declaration> declarationPaths = name.getDeclarationPath();
    if(declarationPaths == null) declarationPaths = new ArrayList<Declaration>();
    declarationPaths.add(name.getDeterminedDeclaration());
    int startIdx = 0;
    for (; startIdx < declarationPaths.size(); startIdx++) {
      Declaration curr = declarationPaths.get(startIdx);
      if(curr instanceof LocalVariableDeclaration) {
        LocalVariableDeclaration localVariableDeclaration = (LocalVariableDeclaration) curr;
        output.println(String.format("lea eax, [ebp - %d]", localVariableDeclaration.offset));
        break;
      } else if(curr instanceof FormalParameter) {
        FormalParameter formalParameter = (FormalParameter) curr;
        output.println(String.format("lea eax, [ebp + %d]", formalParameter.offset));
        break;
      } else if(curr instanceof FieldDeclaration) {
        FieldDeclaration fieldDeclaration = (FieldDeclaration) curr;
        if(fieldDeclaration.containsModifier("static")) {
          genUniqueImport(fieldDeclaration);
          output.println(String.format("lea eax, [%s]", CodeGenUtils.genLabel(fieldDeclaration)));
        } else {
          output.println(String.format("mov eax, [ebp + %d]", thisOffset));
          output.println(String.format("lea eax, [eax + %d]", fieldDeclaration.offset));
        }
        break;
      }
    }

    for(startIdx = startIdx + 1; startIdx < declarationPaths.size(); startIdx++) {
      // Safe to assume that no static accesses here
      output.println("mov eax, [eax]");
      checkForNull("eax");

      Declaration curr = declarationPaths.get(startIdx);
      if(curr instanceof FieldDeclaration) {
        output.println(String.format("lea eax, [eax + %d]", ((FieldDeclaration) curr).offset));
      }
    }

    output.println("; END: generateNameAccessForReference");
  }

  private boolean isTestMethod(MethodDeclaration token) {
    return token.getIdentifier().equals("test") &&
        token.getParameters().isEmpty() &&
        token.methodHeader.type.getType().getLexeme().equals("int") &&
        token.methodHeader.modifiers.containsModifier("static") &&
        token.methodHeader.modifiers.getModifiers().size() == 2;
  }

  @Override
  public void visit(AbstractMethodDeclaration token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(FormalParameterList token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(WhileStatementNoShortIf token) throws VisitorException {
    super.visit(token);
    addComment("WhileStatementNoShortIf " + token.getLexeme());
    whileStatementHelper(token);
  }

  @Override
  public void visit(VariableDeclarator token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(StatementWithoutTrailingSubstatement token) throws VisitorException {
    super.visit(token);
    addComment("StatementWithoutTrailingSubstatement " + token.getLexeme());
    visitEveryChild(token);
  }

  @Override
  public void visit(Type token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(ReferenceType token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(Statement token) throws VisitorException {
    super.visit(token);
    addComment("Statement " + token.getLexeme());
    visitEveryChild(token);
  }

  @Override
  public void visit(FormalParameter token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(CompilationUnit token) throws VisitorException {
    super.visit(token);
    output.println("section .text");
    output.println("extern __malloc");
    output.println("extern __exception");
    output.println("extern NATIVEjava.io.OutputStream.nativeWrite");

    if (token.importDeclarations != null) token.importDeclarations.traverse(this);
    token.typeDeclaration.traverse(this);
  }

  @Override
  public void visit(ConstructorDeclarator token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(Super token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(PrimitiveType token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(MethodHeader token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(BlockStatements token) throws VisitorException {
    super.visit(token);
    addComment("BlockStatement " + token.getLexeme());
    visitEveryChild(token);
  }

  @Override
  public void visit(StatementExpression token) throws VisitorException {
    super.visit(token);
    addComment("StatementExpression " + token.getLexeme());
    visitEveryChild(token);
  }

  @Override
  public void visit(ForInit token) throws VisitorException {
    super.visit(token);
    visit(token.children.get(0));
  }

  @Override
  public void visit(LocalVariableDeclarationStatement token) throws VisitorException {
    super.visit(token);
    addComment("LocalVariableDeclarationStatement " + token.getLexeme());
    visitEveryChild(token);
  }

  @Override
  public void visit(ImportDeclarations token) throws VisitorException {
    super.visit(token);
    for (ImportDeclaration importDeclaration : token.importDeclarations) {
      importDeclaration.traverse(this);
    }
    // Extern all java.lang.* explicitly
    List<Token> javaLangClasses = table.findWithPrefixOfAnyType(token.getLexeme(), new Class[] {ClassDeclaration.class});
    for (Token javaLangClass : javaLangClasses) {
      ClassDeclaration classDeclaration = (ClassDeclaration) javaLangClass;
      for (MethodDeclaration method : classDeclaration.methods) {
        genUniqueImport(method);
      }
      for (FieldDeclaration field : classDeclaration.fields) {
        if (field.containsModifier("static")) {
          genUniqueImport(field);
        }
      }
    }
  }

  @Override
  public void visit(Block token) throws VisitorException {
    super.visit(token);
    visitEveryChild(token);
  }

  @Override
  public void visit(ArrayType token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(InterfaceType token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(ForUpdate token) throws VisitorException {
    super.visit(token);
    visit(token.children.get(0));
  }

  @Override
  public void visit(EmptyStatement token) throws VisitorException {
    super.visit(token);
    addComment("EmptyStatement " + token.getLexeme());
    return;
  }

  @Override
  public void visit(ClassType token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(Name token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(ExtendsInterfaces token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(ClassBodyDeclarations token) throws VisitorException {
    super.visit(token);
    for (ClassBodyDeclaration bodyDeclaration : token.bodyDeclarations) {
      bodyDeclaration.traverse(this);
    }
  }

  @Override
  public void visit(ConstructorBody token) throws VisitorException {
    super.visit(token);
    visitEveryChild(token);
  }

  @Override
  public void visit(ClassDeclaration token) throws VisitorException {
    super.visit(token);
    output.println("; CODE GENERATION: ClassDeclaration");

    token.classBody.traverse(this);

    // After generating code for the class subtree, at the end we create the vtable entry.
    output.println(String.format("; Generating code for %s vtable", token.getAbsolutePath()));
    output.println(String.format("global __vtable__%s", token.getAbsolutePath()));
    output.println(String.format("__vtable__%s: dd 0", token.getAbsolutePath()));
    output.println(String.format("mov eax, %d", token.vTableSize));
    output.println("call __malloc");
    output.println(String.format("mov [__vtable__%s], eax", token.getAbsolutePath()));
    output.println(String.format("mov dword [__vtable__%s], %d", token.getAbsolutePath(), token.classId));
    for (int i = 0; i < token.methods.size(); ++i) {
      output.println(String.format("; Loading address of method decl: %s", token.methods.get(i).getAbsolutePath()));
      output.println(String.format("lea [__vtable__%s + %d], %s", token.getAbsolutePath(), 4 * (i + 1),
          CodeGenUtils.genLabel(token.methods.get(i))));
    }
    // Additionally, we generate the vtable for the Array type.
    if (objectDeclaration != null) {
      output.println(String.format("; Generating code for the %s array vtable", token.getAbsolutePath()));
      output.println(String.format("global __vtable__%s_array", token.getAbsolutePath()));
      output.println(String.format("__vtable__%s_array: dd 0", token.getAbsolutePath()));
      output.println(String.format("mov eax, %d", objectDeclaration.vTableSize));
      output.println("call __malloc");
      output.println(String.format("mov [__vtable__%s_array], eax", token.getAbsolutePath()));

      output.println(String.format("mov dword [__vtable__%s_array], %d", token.getAbsolutePath(), token.classId + numUnits));
      for (int i = 0; i < objectDeclaration.methods.size(); ++i) {
        output.println(String.format("lea [__vtable__%s_array + %d], %s", token.getAbsolutePath(), 4 * (i + 1),
            CodeGenUtils.genLabel(objectDeclaration.methods.get(i))));
      }
    }
    output.println("; END ClassDeclaration");
  }

  @Override
  public void visit(InterfaceMemberDeclaration token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(ForStatementNoShortIf token) throws VisitorException {
    super.visit(token);
    addComment("ForStatementNoShortIf " + token.getLexeme());
    forLoopVisitHelper(token);
  }

  @Override
  public void visit(ArgumentList token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(MethodBody token) throws VisitorException {
    super.visit(token);
    if (token.block != null) visit(token.block);
  }

  @Override
  public void visit(Token token) throws VisitorException {
    super.visit(token);
    if (token.getLexeme().equals("{")) {
      declStack.push(new Stack<LocalVariableDeclaration>());
    } else if (token.getLexeme().equals("}")) {
      while (!declStack.peek().empty()) {
        LocalVariableDeclaration decl = declStack.peek().pop();
        offset -= CodeGenUtils.getSize(decl.type.getType().getLexeme());
      }
      declStack.pop();
    }
  }

  private void checkForNull(String register) {
    output.println("; CODE GENERATION: checkForNull");
    String startLabel = CodeGenUtils.genNextTempLabel();
    output.println(String.format("cmp %s, 0", register));
    output.println(String.format("jne %s", startLabel));
    output.println("call __exception");
    output.println(startLabel + ":");
    output.println("; END: checkForNull");
  }

  private void ifThenElseVisitHelper(BaseIfThenElse token) throws VisitorException {
    output.println("; CODE GENERATION: ifThenElseVisitHelper");
    String ifLabel = CodeGenUtils.genNextIfStatementLabel();

    if (token.expression != null) token.expression.traverse(this);

    output.println("cmp eax, 0");
    output.println("je " + ifLabel);

    visit(token.statementNoShortIf);
    output.println(String.format("jmp %s", CodeGenUtils.getCurrentElseStmtLabel()));

    // else
    output.println(String.format("%s:", ifLabel));
    visit(token.getElseStatement());
    if (!(token.getElseStatement() instanceof BaseIfThenElse)) {
      output.println(String.format("%s:", CodeGenUtils.genNextElseStmtLabel()));
    }
    output.println("; END: ifThenElseVisitHelper");
  }

  private void forLoopVisitHelper(BaseForStatement token) throws VisitorException {
    output.println("; CODE GENERATION: forLoopVisitHelper");
    String forLabel = CodeGenUtils.genNextForStatementLabel();
    String endForLabel = "end#" + forLabel;

    token.newScope.traverse(this);
    if (token.forInit != null) visit(token.forInit);

    output.println(String.format("%s:", forLabel));
    if (token.expression != null) token.expression.traverse(this);

    output.println("cmp eax, 0");
    output.println("je " + endForLabel);

    if (token.getStatement() != null) visit(token.getStatement());

    if (token.forUpdate != null) visit(token.forUpdate);

    output.println("jmp " + forLabel);
    output.println(String.format("%s:", endForLabel));
    token.closeScope.traverse(this);
    output.println("; END: forLoopVisitHelper");
  }

  private void whileStatementHelper(BaseWhileStatement token) throws VisitorException {
    output.println("; CODE GENERATION: whileStatementHelper");
    String whileLabel = CodeGenUtils.genNextWhileStmtLabel();
    String endLabel = "end#" + whileLabel;
    output.println(String.format("%s:", whileLabel));
    // Test the expression
    if (token.children.get(2) != null) token.children.get(2).traverse(this);

    // Jump to end of while loop if expression failed
    output.println("cmp eax, 0");
    output.println("je " + whileLabel);

    // while loop content
    visit(token.children.get(4));
    // jump back to expression check
    output.println("jmp " + whileLabel);
    // end of while loop label; use to exit
    output.println(String.format("%s:", endLabel));
    output.println("; END: whileStatementHelper");
  }

  private void visitEveryChild(Token token) throws VisitorException {
    for (Token child : token.children) {
      child.traverse(this);
    }
  }

  private void addComment(String comment) {
    output.println(";" + comment);
  }

  private void constructString(String value) {
    constructCharArray(value);
    output.println("; CODE GENERATION: constructString");
    CodeGenUtils.genPushRegisters(output, true);
    output.println("mov ebx, eax");
    output.println(String.format("mov eax, %d", 8));
    output.println("call __malloc");
    // Push "this" on the stack.
    output.println("push eax");
    output.println("push ebx");
    genUniqueImport("__vtable__java.lang.String");
    output.println(String.format("mov dword [eax], %s", "__vtable__java.lang.String"));
    genUniqueImport("java.lang.String.String#char@");
    output.println(String.format("call %s", "java.lang.String.String#char@"));
    output.println("pop eax");
    output.println("pop eax");
    CodeGenUtils.genPopRegisters(output, true);
    output.println("; END constructString");
  }

  private int constructCharArray(String value) {
    output.println("; CODE GENERATION: constructCharArray");
    output.println(String.format("mov eax, %d", value.length() * 4 + 8));
    output.println("call __malloc");
    // Initialize the array
    String begin = CodeGenUtils.genUniqueLabel();
    String end = CodeGenUtils.genUniqueLabel();
    for (int a = 0; a < value.length(); a++) {
      output.println(String.format("mov dword [eax + %d], '%c'", a * 4 + 8, value.charAt(a)));
    }
    // Move the address of the vtable as 0th index.
    genUniqueImport("__vtable__char_array");
    output.println("lea [eax], __vtable__char_array");
    // Move length as 1st index.
    output.println(String.format("mov dword [eax + 4], %d", value.length()));
    output.println("; END constructCharArray");
    return value.length() * 4 + 8;
  }

  // Do not use EAX or EDX
  // Results are in EAX
  private void concat(String leftRegister, String rightRegister, AdditiveExpression token) {
    boolean isLeftString = token.isLeftString();
    boolean isRightString = token.isRightString();

    output.println("; CODE GENERATION: concat");
    if(!isLeftString || !isRightString) {
      String registerToConvert = isLeftString ? rightRegister : leftRegister;
      TypeCheckToken type = isLeftString ? token.rightType : token.leftType;
      String typeSuffix = type.isPrimitiveType() ? type.tokenType.toString() : "Object";

      // static call
      genUniqueImport("java.lang.String.valueOf#" + typeSuffix);
      methodInvocation("java.lang.String.valueOf#" + typeSuffix, -1, null, registerToConvert);

      // check if toString returned null, if so convert null to null string
      String label = CodeGenUtils.genNextTempLabel();
      output.println("cmp eax, 0");
      output.println(String.format("jne %s", label));
      genUniqueImport("java.lang.String.valueOf#Object");
      methodInvocation("java.lang.String.valueOf#Object", -1, null, "eax");
      output.println(label + ":");
      output.println(String.format("mov %s, eax", registerToConvert));
    }

    List<Token> declarations = this.table.findWithPrefixOfAnyType("java.lang.String.concat", new Class [] {MethodDeclaration.class});
    int concatMethodId = ((MethodDeclaration) declarations.get(0)).methodId;
    methodInvocation(null, concatMethodId, leftRegister, rightRegister);
    output.println("; END: concat");
  }

  // Uses EDX, make sure to not use this as one of the input registers
  private void methodInvocation(String methodName, int methodId, String thisRegister, String parameterRegister) {
    output.println("; CODE GENERATION: CustomMethodInvocation");
    CodeGenUtils.genPushRegisters(output, true);

    // if null static call, else instance call
    if(thisRegister == null) {
      output.println(String.format("mov edx, %s", methodName));
      output.println("push 0");
    } else {
      output.println(String.format("push %s", thisRegister));
      output.println(String.format("mov edx, [%s]", thisRegister));
      output.println(String.format("mov edx, [edx + %d]", methodId * 4 + 4));
    }

    output.println(String.format("push %s", parameterRegister));
    output.println("call edx");

    // pop off arguments
    output.println("pop edx");
    output.println("pop edx");

    CodeGenUtils.genPopRegisters(output, true);
    // eax should have return value
    output.println("; END: CustomMethodInvocation");
  }
}
