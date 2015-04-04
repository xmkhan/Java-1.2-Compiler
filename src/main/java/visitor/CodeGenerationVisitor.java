package visitor;

import algorithm.base.Pair;
import exception.TypeCheckingVisitorException;
import exception.VisitorException;
import symbol.SymbolTable;
import token.AbstractMethodDeclaration;
import token.AdditiveExpression;
import token.AndExpression;
import token.ArgumentList;
import token.ArrayAccess;
import token.ArrayCreationExpression;
import token.ArrayType;
import token.Assignment;
import token.AssignmentExpression;
import token.AssignmentOperator;
import token.Block;
import token.BlockStatement;
import token.BlockStatements;
import token.BooleanLiteral;
import token.CastExpression;
import token.CharLiteral;
import token.ClassBody;
import token.ClassBodyDeclaration;
import token.ClassBodyDeclarations;
import token.ClassDeclaration;
import token.ClassInstanceCreationExpression;
import token.ClassMemberDeclaration;
import token.ClassOrInterfaceType;
import token.ClassType;
import token.CompilationUnit;
import token.ConditionalAndExpression;
import token.ConditionalOrExpression;
import token.ConstructorBody;
import token.ConstructorDeclaration;
import token.ConstructorDeclarator;
import token.Declaration;
import token.EmptyStatement;
import token.EqualityExpression;
import token.Expression;
import token.ExpressionStatement;
import token.ExtendsInterfaces;
import token.FieldAccess;
import token.FieldDeclaration;
import token.ForInit;
import token.ForStatement;
import token.ForStatementNoShortIf;
import token.ForUpdate;
import token.FormalParameter;
import token.FormalParameterList;
import token.IfThenElseStatement;
import token.IfThenElseStatementNoShortIf;
import token.IfThenStatement;
import token.ImportDeclaration;
import token.ImportDeclarations;
import token.InclusiveOrExpression;
import token.IntLiteral;
import token.InterfaceBody;
import token.InterfaceDeclaration;
import token.InterfaceMemberDeclaration;
import token.InterfaceMemberDeclarations;
import token.InterfaceType;
import token.InterfaceTypeList;
import token.Interfaces;
import token.LeftHandSide;
import token.Literal;
import token.LocalVariableDeclaration;
import token.LocalVariableDeclarationStatement;
import token.MethodBody;
import token.MethodDeclaration;
import token.MethodDeclarator;
import token.MethodHeader;
import token.MethodInvocation;
import token.Modifier;
import token.Modifiers;
import token.MultiplicativeExpression;
import token.Name;
import token.PackageDeclaration;
import token.Primary;
import token.PrimitiveType;
import token.QualifiedName;
import token.ReferenceType;
import token.RelationalExpression;
import token.ReturnStatement;
import token.SimpleName;
import token.SingleTypeImportDeclaration;
import token.Statement;
import token.StatementExpression;
import token.StatementNoShortIf;
import token.StatementWithoutTrailingSubstatement;
import token.StringLiteral;
import token.Super;
import token.Token;
import token.TokenType;
import token.Type;
import token.TypeDeclaration;
import token.TypeImportOnDemandDeclaration;
import token.UnaryExpression;
import token.UnaryExpressionNotMinus;
import token.VariableDeclarator;
import token.WhileStatement;
import token.WhileStatementNoShortIf;
import type.hierarchy.HierarchyGraph;
import type.hierarchy.HierarchyGraphNode;
import util.CodeGenUtils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
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
  public PrintStream output;
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

    // Find the Object declaration that is used by Arrays.
    for (CompilationUnit unit : units) {
      if (unit.typeDeclaration.getDeclaration().getAbsolutePath().equals("java.lang.Object")) {
        objectDeclaration = (ClassDeclaration) unit.typeDeclaration.getDeclaration();
      }
    }

    for (CompilationUnit unit : units) {
      output = new PrintStream(new FileOutputStream(String.format("output/%s.o",unit.typeDeclaration.getDeclaration().getIdentifier())));
      unit.traverse(this);
    }
    output = new PrintStream(new FileOutputStream("output/__program.o"));
    genSubtypeTable();
    genSelectorIndexTable();
  }

  private void genSubtypeTable() {
    output.println("; CODE GENERATION: genSubtypeTable");
    output.println("global __subtype_table");
    output.println("__subtype_table: dd");
    output.println(String.format("mov eax, %d",(4 * subclassTable.length)));
    output.println("call __malloc");
    output.println("mov __subtype_table, eax");
    for(int i = 0; i < subclassTable.length; ++i) {
      output.println(String.format("mov eax, %d", (4 * subclassTable[i].length)));
      output.println("call __malloc");
      output.println(String.format("mov [__subtype_table + %d], eax", 4 * i));
      output.println(String.format("mov ebx, [__subtype_table + %d]", 4 * i));
      for (int j = 0; j < subclassTable[i].length; ++j) {
        output.println(String.format("mov [ebx + %d], %d", 4 * j, subclassTable[i][j] ? 1 : 0));
      }
    }
    output.println("; END genSubtypeTable");
  }

  private void genSelectorIndexTable() {
    output.println("; CODE GENERATION: genSelectorIndexTable");
    output.println("global __selector_index_table");
    output.println("__selector_index_table: dd");
    output.println(String.format("mov eax, %d",(4 * selectorIndexTable.length)));
    output.println("call __malloc");
    output.println("mov __selector_index_table, eax");
    for(int i = 0; i < selectorIndexTable.length; ++i) {
      output.println(String.format("mov eax, %d", (4 * selectorIndexTable.length)));
      output.println("call __malloc");
      output.println(String.format("mov [__selector_index_table + %d], eax", 4 * i));
      output.println(String.format("mov ebx, [__selector_index_table + %d]", 4 * i));
      for (int j = 0; j < selectorIndexTable[i].length; ++j) {
        if (selectorIndexTable[i][j] == null) output.println(String.format("mov [ebx + %d], 0", 4 * j));
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
        output.println(String.format("global __vtable_%s_array", name));
        output.println(String.format("__vtable_%s_array: dd", name));
        output.println(String.format("mov eax, %d", objectDeclaration.vTableSize));
        output.println("call __malloc");
        output.println(String.format("mov __vtable_%s_array, eax", name));
        output.println(String.format("mov [__vtable_%s_array], %d", name, numUnits + i));
        for (int j = 1; j < objectDeclaration.methods.size(); ++j) {
          output.println(String.format("lea [__vtable_%s_array + %d], %s", name, 4 * j,
              CodeGenUtils.genLabel(objectDeclaration.methods.get(j))));
        }
      }
      output.println("; END genPrimitiveArrayVTable");
    }
  }

  @Override
  public void visit(ExpressionStatement token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(FieldDeclaration token) throws VisitorException {
    super.visit(token);
    output.println("; CODE GENERATION: FieldDeclaration");
    int fieldSize = CodeGenUtils.getSize(token.type.getType().getLexeme());
    if (token.containsModifier("static")) {
      output.println(String.format("global %s", token.getAbsolutePath()));
      output.println(String.format("%s: %s", token.getAbsolutePath(), CodeGenUtils.getReserveSize(fieldSize)));
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
  }

  @Override
  public void visit(WhileStatement token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(Modifier token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(IfThenStatement token) throws VisitorException {
    super.visit(token);
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
  }

  @Override
  public void visit(ClassBodyDeclaration token) throws VisitorException {
    super.visit(token);
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
  }

  @Override
  public void visit(QualifiedName token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(ClassBody token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(ForStatement token) throws VisitorException {
    super.visit(token);
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
    output.println("; Literal");

    Token literal = token.getLiteral();

    if(token.isStringLiteral()) {
      //TODO: Handle Strings
      /*
      +    ConstructorDeclaration constructorDeclaration = (ConstructorDeclaration) token.classType.classOrInterfaceType.name.getDeterminedDeclaration();
      +    ClassDeclaration classDeclaration =  (ClassDeclaration) table.getClass(constructorDeclaration);
      +    output.println(String.format("mov eax, %d", classDeclaration.classSize));
      +    output.println("call __malloc");
      +    // Push "this" on the stack.
              +    output.println("push eax");
      +    // For all non-static fields, initialize them before calling the specified constructor.
              +    for (FieldDeclaration field : classDeclaration.fields) {
        +      if (!field.containsModifier("static")) {
          +        visit(field);
          +      }
        +    }
*/
    } else if(token.isCharLiteral()) {
      char value = token.getLexeme().charAt(0);
      output.println(String.format("mov eax, '%c'", value));
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
  }

  @Override
  public void visit(SimpleName token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(IfThenElseStatement token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(TypeDeclaration token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(Expression token) throws VisitorException {
    super.visit(token);
    token.children.get(0).traverse(this);
  }

  @Override
  public void visit(MultiplicativeExpression token) throws VisitorException {
    super.visit(token);
    if(token.isDefined()) {
      output.println("; MultiplicativeExpression");
      token.expr1.traverse(this);
      output.println("push eax");
      token.expr.traverse(this);
      output.println("pop ebx");
      if(token.operator.getTokenType().equals(TokenType.MULT_OP)) {
        output.println("imul eax, ebx");
      } else {
        String startLabel = CodeGenUtils.genNextTempLabel();
        String endLabel = CodeGenUtils.genNextTempLabel();
        output.println("cmp ebx, 0");
        output.println(String.format("jne %s", startLabel));
        output.println("call __exception");
        output.println(startLabel);
        output.println("mov edx, 0");
        output.println("cmp eax, 0");
        output.println(String.format("jge %s", endLabel));
        output.println("mov edx, -1");
        output.println(endLabel);
        output.println("idiv ebx");

        if(token.operator.getTokenType().equals(TokenType.MOD_OP)) {
          output.println("mov eax, edx");
        }
      }
    } else {
      token.expr1.traverse(this);
    }
  }

  @Override
  public void visit(AdditiveExpression token) throws VisitorException {
    super.visit(token);
    //TODO: Handle strings
    if(token.isDefined()) {
      output.println("; AdditiveExpression");
      token.leftExpr.traverse(this);
      output.println("push eax");
      token.rightExpr.traverse(this);
      output.println("pop ebx");
      if(token.isAdd()) {
        output.println("addl eax, ebx");
      } else {
        output.println("subl ebx, eax");
        output.println("mov eax, ebx");
      }
    } else {
      token.rightExpr.traverse(this);
    }
  }

  @Override
  public void visit(RelationalExpression token) throws VisitorException {
    super.visit(token);
    if(token.isDefined()) {
      output.println("; RelationalExpression");
      token.leftExpr.traverse(this);
      output.println("push eax");
      token.rightExpr.traverse(this);
      output.println("pop ebx");
      output.println("mov ecx, eax");
      output.println("mov eax, 0");
      if(token.getOperator().getTokenType().equals(TokenType.INSTANCEOF)) {
        //TODO: handle instanceof
      } else {
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
        output.println(endLabel);
      }
    } else {
      token.rightExpr.traverse(this);
    }
  }

  @Override
  public void visit(EqualityExpression token) throws VisitorException {
    super.visit(token);
    if(token.isDefined()) {
      String endLabel = CodeGenUtils.genNextTempLabel();

      output.println("; EqualityExpression");
      token.leftExpr.traverse(this);
      output.println("push eax");
      token.rightExpr.traverse(this);
      output.println("pop ebx");
      output.println("mov ecx, eax");
      output.println("mov eax, 0");
      output.println("cmpl ebx, ecx");
      if (token.isEqualCheck()) {
        output.println(String.format("jne %s", endLabel));
      } else {
        output.println(String.format("je %s", endLabel));
      }
      output.println("mov eax, 1");
      output.println(endLabel);
    } else {
      token.rightExpr.traverse(this);
    }
  }

  @Override
  public void visit(AndExpression token) throws VisitorException {
    super.visit(token);
    if(token.isDefined()) {
//      String endLabel = CodeGenUtils.genNextTempLabel();

      output.println("; AndExpression");
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
//      output.println(endLabel);
    } else {
      token.rightExpr.traverse(this);
    }
  }

  @Override
  public void visit(InclusiveOrExpression token) throws VisitorException {
    super.visit(token);
    if (token.isDefined()) {
//      String endLabel = CodeGenUtils.genNextTempLabel();

      output.println("; InclusiveOrExpression");
      token.leftExpr.traverse(this);
      output.println("push eax");
      token.rightExpr.traverse(this);
      output.println("pop ebx");
      output.println("or eax, ebx");
//      output.println("add eax, ebx");
//      output.println("cmp eax, 2");
//      output.println(String.format("jne %s", endLabel));
//      output.println("mov eax, 1");
//      output.println(endLabel);
    } else {
      token.rightExpr.traverse(this);
    }
  }

  @Override
  public void visit(ConditionalOrExpression token) throws VisitorException {
    super.visit(token);
    if(token.isDefined()) {
      String endLabel = CodeGenUtils.genNextTempLabel();

      output.println("; ConditionalOrExpression");
      token.leftExpr.traverse(this);
      output.println("cmp eax, 1");
      output.println(String.format("je %s", endLabel));
      token.rightExpr.traverse(this);
      output.println(endLabel);
    } else {
      token.rightExpr.traverse(this);
    }
  }

  @Override
  public void visit(ConditionalAndExpression token) throws VisitorException {
    super.visit(token);
    if(token.isDefined()) {
      String endLabel = CodeGenUtils.genNextTempLabel();

      output.println("; ConditionalAndExpression");
      token.leftExpr.traverse(this);
      output.println("cmp eax, 0");
      output.println(String.format("je %s", endLabel));
      token.rightExpr.traverse(this);
      output.println(endLabel);
    } else {
      token.rightExpr.traverse(this);
    }
  }

  @Override
  public void visit(Primary token) throws VisitorException {
    super.visit(token);
    if(token.children.size() == 1) {
      if(token.children.get(0).getTokenType() == TokenType.THIS) {
        output.println(String.format("mov eax, [ebp + %d]", thisOffset));
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
    token.primary.traverse(this);
    checkForNull("eax");

    if(token.primary.getDeterminedType().isArray) {
      output.println("mov eax, [eax + 4]");
    } else {
      FieldDeclaration decl = (FieldDeclaration) token.getDeterminedDeclaration();
      output.println(String.format("mov eax, [eax + %d]", decl.offset));
    }
  }

  @Override
  public void visit(ArrayAccess token) throws VisitorException {
    super.visit(token);
    output.println("; ArrayAccess start");
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
    output.println(label);
    output.println("mov eax, [ebx + eax * 4 + 8]");
    output.println("; ArrayAccess end");
  }

  @Override
  public void visit(MethodInvocation token) throws VisitorException {
    super.visit(token);
    CodeGenUtils.genPushRegisters(output, true);
    if(token.isOnPrimary()) {
      token.primary.traverse(this);
      checkForNull("eax");

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

    //eax now points to the method to call and this is on stack if non-static call
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

    CodeGenUtils.genPopRegisters(output, true);
    // eax should have return value
  }

  @Override
  public void visit(LeftHandSide token) throws VisitorException {
    super.visit(token);
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
      output.println(label);
      output.println("lea eax, [ebx + eax * 4 + 8]");
    }
  }

  @Override
  public void visit(AssignmentExpression token) throws VisitorException {
    super.visit(token);
    token.children.get(0).traverse(this);
  }

  @Override
  public void visit(Assignment token) throws VisitorException {
    super.visit(token);
    token.leftHandSide.traverse(this);
    output.println("push eax");
    token.assignmentExpression.traverse(this);
    output.println("pop ebx");
    output.println("mov [ebx], eax");
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
    output.println("mov [eax + ecx * 4], 0");
    output.println("inc ecx");
    output.println(String.format("jmp %s", begin));
    output.println(String.format("%s:", end));
    // Move the address of the vtable as 0th index.
    output.println(String.format("lea [eax], __vtable_%s_array", vTableName));
    // Move length as 1st index.
    output.println("mov [eax + 4], ebx");
    CodeGenUtils.genPopRegisters(output, true);
    output.println("; END ArrayCreationExpression");
  }

  @Override
  public void visit(CastExpression token) throws VisitorException {
    super.visit(token);
    if(token.isArrayCast()) {
      token.children.get(5).traverse(this);
    } else {
      token.children.get(3).traverse(this);
    }

    if(token.isArrayCast() || token.isName()) {
      int classId;
      if(token.isName()) {
        Declaration decl = token.name.getDeterminedDeclaration();
        if(decl instanceof ClassDeclaration) {
          classId = ((ClassDeclaration) decl).classId;
        } else {
          classId = ((InterfaceDeclaration) decl).classId;
        }

        if(token.isArrayCast()) {
          classId += numUnits;
        }
      } else {
        classId = CodeGenUtils.getArrayPrimitiveClassId(numUnits, token.primitiveType.getType().getLexeme());
      }

      output.println();
    } else {

    }
  }

  @Override
  public void visit(UnaryExpression token) throws VisitorException {
    super.visit(token);
    if(token.isNegative()) {
      output.println("; UnaryExpression");
      token.exp.traverse(this);
      output.println("neg eax");
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
    } else if(token.name != null) {
      output.println("; UnaryExpressionNotMinus");
      generateNameAccess(token.name, false);
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
      output.println(String.format("call %s.%s#void", baseClass.getAbsolutePath(), baseClass.getIdentifier()));
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
    offset = 0;
    token.body.traverse(this);
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
  }

  @Override
  public void visit(StatementNoShortIf token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(BlockStatement token) throws VisitorException {
    super.visit(token);
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
    CodeGenUtils.genPushRegisters(output, false);
    if (token.argumentList != null && !token.argumentList.argumentList.isEmpty()) {
      for (Expression expr : token.argumentList.argumentList) {
        expr.traverse(this);
        output.println("push eax");
      }
    }
    output.println(String.format("mov eax, %d", classDeclaration.classSize));
    output.println("call __malloc");
    // Push "this" on the stack.
    output.println("push eax");
    // For all non-static fields, initialize them before calling the specified constructor.
    for (FieldDeclaration field : classDeclaration.fields) {
      if (!field.containsModifier("static")) {
        field.traverse(this);
      }
    }
    output.println(String.format("call %s", CodeGenUtils.genLabel(constructorDeclaration)));
    CodeGenUtils.genPopRegisters(output, false);
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
    thisOffset = token.methodHeader.modifiers.containsModifier("static") ? paramOffset : 0;

    offset = 0;
    token.methodBody.traverse(this);
    output.println("; END MethodDeclaration");
  }

  private void generateNameAccess(Name name, boolean pushThisOnStackForMethod) {
    output.println("; generateNameAccess start");
    boolean thisKeptTrack = false;
    List<Declaration> declarationPaths = name.getDeclarationPath();
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
          output.println(String.format("mov eax, [%s]", fieldDeclaration.getAbsolutePath()));
        } else {
          output.println(String.format("mov eax, [ebp + %d]", thisOffset));
          output.println(String.format("mov eax, [eax + %d]", fieldDeclaration.offset));
        }
        break;
      } else if (curr instanceof MethodDeclaration) {
        MethodDeclaration methodDeclaration = (MethodDeclaration) curr;
        if(methodDeclaration.methodHeader.containsModifier("static")) {
          String nativePrefix = methodDeclaration.methodHeader.containsModifier("native") ? "NATIVE" : "";
          output.println(String.format("mov eax, %s", nativePrefix + CodeGenUtils.genLabel(methodDeclaration)));
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

    if(thisKeptTrack) {
      output.println("push ecx");
    }
    output.println("; generateNameAccess end");
  }

  private void generateNameAccessForReference(Name name) {
    output.println("; generateNameAccessForReference start");
    List<Declaration> declarationPaths = name.getDeclarationPath();
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
          output.println(String.format("lea eax, [%s]", fieldDeclaration.getAbsolutePath()));
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

    output.println("; generateNameAccessForReference end");
  }

  private boolean isTestMethod(MethodDeclaration token) {
    return token.getIdentifier().equals("test") &&
        token.getParameters().isEmpty() &&
        token.methodHeader.type.getType().getLexeme().equals("int") &&
        token.methodHeader.modifiers.containsModifier("static") &&
        token.methodHeader.modifiers.getModifiers().size() == 1;
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
  }

  @Override
  public void visit(VariableDeclarator token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(StatementWithoutTrailingSubstatement token) throws VisitorException {
    super.visit(token);
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
  }

  @Override
  public void visit(FormalParameter token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(CompilationUnit token) throws VisitorException {
    super.visit(token);
    output.println("section .text");
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
  }

  @Override
  public void visit(StatementExpression token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(ForInit token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(LocalVariableDeclarationStatement token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(ImportDeclarations token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(Block token) throws VisitorException {
    super.visit(token);
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
  }

  @Override
  public void visit(EmptyStatement token) throws VisitorException {
    super.visit(token);
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
  }

  @Override
  public void visit(ConstructorBody token) throws VisitorException {
    super.visit(token);
  }

  @Override
  public void visit(ClassDeclaration token) throws VisitorException {
    super.visit(token);
    output.println("; CODE GENERATION: ClassDeclaration");

    // After generating code for the class subtree, at the end we create the vtable entry.
    output.println(String.format("; Generating code for %s vtable", token.getAbsolutePath()));
    output.println(String.format("global __vtable_%s", token.getAbsolutePath()));
    output.println(String.format("__vtable_%s: dd", token.getAbsolutePath()));
    output.println(String.format("mov eax, %d", token.vTableSize));
    output.println("call __malloc");
    output.println(String.format("mov __vtable_%s, eax", token.getAbsolutePath()));
    output.println(String.format("mov [__vtable_%s], %d", token.getAbsolutePath(), token.classId));
    for (int i = 1; i < token.methods.size(); ++i) {
      output.println(String.format("; Loading address of method decl: %s", token.methods.get(i).getAbsolutePath()));
      output.println(String.format("lea [__vtable_%s + %d], %s", token.getAbsolutePath(), 4 * i, CodeGenUtils.genLabel(token.methods.get(i))));
    }
    // Additionally, we generate the vtable for the Array type.
    if (objectDeclaration != null) {
      output.println(String.format("; Generating code for the %s array vtable", token.getAbsolutePath()));
      output.println(String.format("global __vtable_%s_array", token.getAbsolutePath()));
      output.println(String.format("__vtable_%s_array: dd", token.getAbsolutePath()));
      output.println(String.format("mov eax, %d", objectDeclaration.vTableSize));
      output.println("call __malloc");
      output.println(String.format("mov __vtable_%s_array, eax", token.getAbsolutePath()));

      output.println(String.format("mov [__vtable_%s_array], %d", token.getAbsolutePath(), token.classId + numUnits));
      for (int i = 1; i < objectDeclaration.methods.size(); ++i) {
        output.println(String.format("lea [__vtable_%s_array + %d], %s", token.getAbsolutePath(), 4 * i,
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
      while(!declStack.peek().empty()) {
        LocalVariableDeclaration decl = declStack.peek().pop();
        offset -= CodeGenUtils.getSize(decl.type.getType().getLexeme());
      }
      declStack.pop();
    }
  }

  private void checkForNull(String register) {
    String startLabel = CodeGenUtils.genNextTempLabel();
    output.println(String.format("cmp %s, 0", register));
    output.println(String.format("jne %s", startLabel));
    output.println("call __exception");
    output.println(startLabel);
  }
}
