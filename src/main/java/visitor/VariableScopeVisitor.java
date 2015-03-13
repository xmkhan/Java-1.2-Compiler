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
  private final SymbolTable symbolTable;
  private SymbolTable variableTable;

  public VariableScopeVisitor(SymbolTable symbolTable) {
    this.symbolTable = symbolTable;
    variableTable = new SymbolTable();
  }

  protected SymbolTable getVariableTable() {
    return variableTable;
  }

  protected SymbolTable getSymbolTable() {
    return variableTable;
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
    }
  }
}

