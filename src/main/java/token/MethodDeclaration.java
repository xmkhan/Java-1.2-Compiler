package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;
import java.util.List;

public class MethodDeclaration extends BaseMethodDeclaration {

  public MethodHeader methodHeader;
  public MethodBody methodBody;

  public Token newScope;
  public Token closeScope;

  public int methodId = -1;

  public MethodDeclaration(ArrayList<Token> children) {
    super("", TokenType.MethodDeclaration, children);
    methodHeader = (MethodHeader) children.get(0);
    methodBody = (MethodBody) children.get(1);
    identifier = methodHeader.identifier;
    type = methodHeader.type;
    // To handle implicit scopes, we explicitly add the scope.
    newScope = new Token("{", TokenType.LEFT_BRACE);
    closeScope = new Token("}", TokenType.RIGHT_BRACE);
  }

  public List<FormalParameter> getParameters() {
    if (methodHeader.paramList == null || methodHeader.paramList.params == null) return new ArrayList<FormalParameter>();
    return methodHeader.paramList.params;
  }

  @Override
  public void accept(Visitor v) throws VisitorException {
    newScope.accept(v);
    methodHeader.accept(v);
    methodBody.accept(v);
    closeScope.accept(v);
    v.visit(this);
  }

  @Override
  public void acceptReverse(Visitor v) throws VisitorException {
    v.visit(this);
    newScope.acceptReverse(v);
    methodHeader.acceptReverse(v);
    methodBody.acceptReverse(v);
    closeScope.acceptReverse(v);
  }

  @Override
  public void traverse(Visitor v) throws VisitorException {
    v.visit(this);
  }
}
