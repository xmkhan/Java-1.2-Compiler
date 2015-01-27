package token;

import java.util.ArrayList;
import visitor.Visitor;

public class MethodDeclaration extends Token {

  public MethodHeader methodHeader;
  public MethodBody methodBody;

  public MethodDeclaration(ArrayList<Token> children) {
    super("", TokenType.MethodDeclaration);
    methodHeader = (MethodHeader) children.get(0);
    methodBody = (MethodBody) children.get(1);
  }

  public void accept(Visitor v) {
    v.visit(methodHeader);
    v.visit(methodBody);
    v.visit(this);
  }
}
