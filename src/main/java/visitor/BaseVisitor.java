package visitor;

import exception.VisitorException;
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
import token.Type;
import token.TypeDeclaration;
import token.TypeImportOnDemandDeclaration;
import token.UnaryExpression;
import token.UnaryExpressionNotMinus;
import token.VariableDeclarator;
import token.WhileStatement;
import token.WhileStatementNoShortIf;

public class BaseVisitor implements Visitor {

  public void visit(ExpressionStatement token) throws VisitorException {
  }

  public void visit(FieldDeclaration token) throws VisitorException {
  }

  public void visit(PackageDeclaration token) throws VisitorException {
  }

  public void visit(InterfaceMemberDeclarations token) throws VisitorException {
  }

  public void visit(MethodInvocation token) throws VisitorException {
  }

  public void visit(Interfaces token) throws VisitorException {
  }

  public void visit(ClassMemberDeclaration token) throws VisitorException {
  }

  public void visit(WhileStatement token) throws VisitorException {
  }

  public void visit(Modifier token) throws VisitorException {
  }

  public void visit(Primary token) throws VisitorException {
  }

  public void visit(IfThenStatement token) throws VisitorException {
  }

  public void visit(Modifiers token) throws VisitorException {
  }

  public void visit(InterfaceDeclaration token) throws VisitorException {
  }

  public void visit(CastExpression token) throws VisitorException {
  }

  public void visit(SingleTypeImportDeclaration token) throws VisitorException {
  }

  public void visit(ImportDeclaration token) throws VisitorException {
  }

  public void visit(ClassBodyDeclaration token) throws VisitorException {
  }

  public void visit(InterfaceBody token) throws VisitorException {
  }

  public void visit(ClassOrInterfaceType token) throws VisitorException {
  }

  public void visit(IfThenElseStatementNoShortIf token) throws VisitorException {
  }

  public void visit(QualifiedName token) throws VisitorException {
  }

  public void visit(InclusiveOrExpression token) throws VisitorException {
  }

  public void visit(ClassBody token) throws VisitorException {
  }

  public void visit(ForStatement token) throws VisitorException {
  }

  public void visit(ConditionalAndExpression token) throws VisitorException {
  }

  public void visit(TypeImportOnDemandDeclaration token) throws VisitorException {
  }

  public void visit(AssignmentOperator token) throws VisitorException {
  }

  public void visit(Literal token) throws VisitorException {
  }

  public void visit(AndExpression token) throws VisitorException {
  }

  public void visit(SimpleName token) throws VisitorException {
  }

  public void visit(ArrayCreationExpression token) throws VisitorException {
  }

  public void visit(CharLiteral token) throws VisitorException {
  }

  public void visit(IfThenElseStatement token) throws VisitorException {
  }

  public void visit(TypeDeclaration token) throws VisitorException {
  }

  public void visit(ConditionalOrExpression token) throws VisitorException {
  }

  public void visit(ConstructorDeclaration token) throws VisitorException {
  }

  public void visit(LocalVariableDeclaration token) throws VisitorException {
  }

  public void visit(InterfaceTypeList token) throws VisitorException {
  }

  public void visit(ReturnStatement token) throws VisitorException {
  }

  public void visit(UnaryExpressionNotMinus token) throws VisitorException {
  }

  public void visit(RelationalExpression token) throws VisitorException {
  }

  public void visit(StatementNoShortIf token) throws VisitorException {
  }

  public void visit(BlockStatement token) throws VisitorException {
  }

  public void visit(MethodDeclarator token) throws VisitorException {
  }

  public void visit(UnaryExpression token) throws VisitorException {
  }

  public void visit(AdditiveExpression token) throws VisitorException {
  }

  public void visit(ClassInstanceCreationExpression token) throws VisitorException {
  }

  public void visit(MethodDeclaration token) throws VisitorException {
  }

  public void visit(AbstractMethodDeclaration token) throws VisitorException {
  }

  public void visit(MultiplicativeExpression token) throws VisitorException {
  }

  public void visit(FormalParameterList token) throws VisitorException {
  }

  public void visit(WhileStatementNoShortIf token) throws VisitorException {
  }

  public void visit(VariableDeclarator token) throws VisitorException {
  }

  public void visit(AssignmentExpression token) throws VisitorException {
  }

  public void visit(StatementWithoutTrailingSubstatement token) throws VisitorException {
  }

  public void visit(Type token) throws VisitorException {
  }

  public void visit(ReferenceType token) throws VisitorException {
  }

  public void visit(Statement token) throws VisitorException {
  }

  public void visit(FormalParameter token) throws VisitorException {
  }

  public void visit(LeftHandSide token) throws VisitorException {
  }

  public void visit(Assignment token) throws VisitorException {
  }

  public void visit(CompilationUnit token) throws VisitorException {
  }

  public void visit(ConstructorDeclarator token) throws VisitorException {
  }

  public void visit(IntLiteral token) throws VisitorException {
  }

  public void visit(Super token) throws VisitorException {
  }

  public void visit(PrimitiveType token) throws VisitorException {
  }

  public void visit(MethodHeader token) throws VisitorException {
  }

  public void visit(BlockStatements token) throws VisitorException {
  }

  public void visit(FieldAccess token) throws VisitorException {
  }

  public void visit(StatementExpression token) throws VisitorException {
  }

  public void visit(ForInit token) throws VisitorException {
  }

  public void visit(LocalVariableDeclarationStatement token) throws VisitorException {
  }

  public void visit(ArrayAccess token) throws VisitorException {
  }

  public void visit(Expression token) throws VisitorException {
  }

  public void visit(ImportDeclarations token) throws VisitorException {
  }

  public void visit(Block token) throws VisitorException {
  }

  public void visit(StringLiteral token) throws VisitorException {
  }

  public void visit(ArrayType token) throws VisitorException {
  }

  public void visit(InterfaceType token) throws VisitorException {
  }

  public void visit(ForUpdate token) throws VisitorException {
  }

  public void visit(EmptyStatement token) throws VisitorException {
  }

  public void visit(ClassType token) throws VisitorException {
  }

  public void visit(Name token) throws VisitorException {
  }

  public void visit(ExtendsInterfaces token) throws VisitorException {
  }

  public void visit(EqualityExpression token) throws VisitorException {
  }

  public void visit(ClassBodyDeclarations token) throws VisitorException {
  }

  public void visit(ConstructorBody token) throws VisitorException {
  }

  public void visit(ClassDeclaration token) throws VisitorException {
  }

  public void visit(InterfaceMemberDeclaration token) throws VisitorException {
  }

  public void visit(ForStatementNoShortIf token) throws VisitorException {
  }

  public void visit(BooleanLiteral token) throws VisitorException {
  }

  public void visit(ArgumentList token) throws VisitorException {
  }

  public void visit(MethodBody token) throws VisitorException {
  }

  public void visit(Token token) throws VisitorException {
  }

}
