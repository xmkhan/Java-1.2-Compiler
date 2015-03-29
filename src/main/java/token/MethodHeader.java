package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class MethodHeader extends Token {

  public Modifiers modifiers;
  public Type type;
  public Token voidType;
  public Token identifier;
  public FormalParameterList paramList;

  public MethodHeader(ArrayList<Token> children) {
    super("", TokenType.MethodHeader, children);
    for (Token token : children) {
      assignType(token);
    }
  }

  private void assignType(Token token) {
    if (token instanceof Type) {
      type = (Type) token;
    } else if (token instanceof Modifiers) {
      modifiers = (Modifiers) token;
    } else if (token.getTokenType() == TokenType.VOID) {
      voidType = token;
    } else if (token instanceof MethodDeclarator) {
      identifier = token.children.get(0);
      paramList = (token.children.size() == 4)? (FormalParameterList) token.children.get(2) : null;
    }
  }

  public boolean isVoid() {
    return voidType != null;
  }

  @Override
  public void accept(Visitor v) throws VisitorException {
    v.visit(modifiers);
    if (type != null) type.accept(v);
    if (paramList != null) paramList.accept(v);
    v.visit(this);
  }

  @Override
  public void acceptReverse(Visitor v) throws VisitorException {
    v.visit(this);
    v.visit(modifiers);
    if (type != null) type.acceptReverse(v);
    if (paramList != null) paramList.acceptReverse(v);
  }

  @Override
  public void traverse(Visitor v) throws VisitorException {
    v.visit(this);
  }
}
