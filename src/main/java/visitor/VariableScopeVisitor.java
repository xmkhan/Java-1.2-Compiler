package visitor;

import exception.VisitorException;
import symbol.SymbolTable;
import token.FieldDeclaration;
import token.FormalParameter;
import token.LocalVariableDeclaration;
import token.Token;

/**
 * Keeps track of the { variableName -> Type } mapping w.r.t to scopes
 */
public class VariableScopeVisitor extends BaseVisitor {
  private SymbolTable variableTable;
  protected FieldDeclaration fieldDeclaration = null;

  public VariableScopeVisitor() {
    variableTable = new SymbolTable();
  }

  protected SymbolTable getVariableTable() {
    return variableTable;
  }

  @Override
  public void visit(FieldDeclaration token) throws VisitorException {
    super.visit(token);
    fieldDeclaration = token;
  }

  @Override
  public void visit(FormalParameter token) throws VisitorException {
    super.visit(token);
    variableTable.addDecl(token.getIdentifier(), token);
  }

  @Override
  public void visit(LocalVariableDeclaration token) throws VisitorException {
    super.visit(token);
    variableTable.addDecl(token.getIdentifier(), token);
  }

  @Override
  public void visit(Token token) throws VisitorException {
    super.visit(token);
    if (token.getLexeme().equals("{")) {
      variableTable.newScope();
    } else if (token.getLexeme().equals("}")) {
      variableTable.deleteScope();
    } else if (token.getLexeme().equals(";") && fieldDeclaration != null) {
      if (!variableTable.contains(fieldDeclaration.getAbsolutePath())) {
        variableTable.addDecl(fieldDeclaration.getIdentifier(), fieldDeclaration);
      }
      fieldDeclaration = null;
    }
  }
}

