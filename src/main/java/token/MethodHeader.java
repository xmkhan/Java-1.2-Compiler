package token;

import java.util.ArrayList;
import visitor.Visitor;
import visitor.VisitorException;

public class MethodHeader extends Token {

  public Modifiers modifiers;
  public Type type;
  public Token voidType;
  public MethodDeclarator methodDeclarator;

  public MethodHeader(ArrayList<Token> children) {
    super("", TokenType.MethodHeader);
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
      methodDeclarator = (MethodDeclarator) token;
    }
  }

  public boolean isVoid() {
    return voidType != null;
  }

  public void accept(Visitor v) throws VisitorException {
    v.visit(methodDeclarator);
    if (modifiers != null) v.visit(modifiers);
    v.visit(this);
  }
}
