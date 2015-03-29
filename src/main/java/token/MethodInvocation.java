package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class MethodInvocation extends Token {

  public Name name;
  public ArgumentList argumentList;
  public Primary primary;
  public Token identifier;

  public MethodInvocation(ArrayList<Token> children) {
    super("", TokenType.MethodInvocation, children);
    for (Token token : children) {
      assignType(token);
    }
  }

  private void assignType(Token token) {
    if (token instanceof Name) {
      name = (Name) token;
    } else if (token.getTokenType() == TokenType.IDENTIFIER) {
      identifier = token;
    } else if (token instanceof ArgumentList) {
      argumentList = (ArgumentList) token;
    } else if (token instanceof Primary) {
      primary = (Primary) token;
    }
  }

  public boolean isOnPrimary() {
    return primary != null;
  }

  public boolean hasArguments() {
    return argumentList != null;
  }

  @Override
  public void accept(Visitor v) throws VisitorException {
    if (name != null) name.accept(v);
    if (primary != null) primary.accept(v);
    if (identifier != null) identifier.accept(v);
    if (argumentList != null) argumentList.accept(v);
    v.visit(this);
  }

  @Override
  public void acceptReverse(Visitor v) throws VisitorException {
    v.visit(this);
    if (name != null) name.acceptReverse(v);
    if (primary != null) primary.acceptReverse(v);
    if (identifier != null) identifier.acceptReverse(v);
    if (argumentList != null) argumentList.acceptReverse(v);
  }

  @Override
  public void traverse(Visitor v) throws VisitorException {
    v.visit(this);
  }
}
