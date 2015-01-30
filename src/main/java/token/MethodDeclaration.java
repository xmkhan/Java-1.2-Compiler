package token;

import java.util.ArrayList;
import visitor.Visitor;
import visitor.VisitorException;

public class MethodDeclaration extends Token {

  public MethodHeader methodHeader;
  public MethodBody methodBody;

  public MethodDeclaration(ArrayList<Token> children) {
    super("", TokenType.MethodDeclaration);
    methodHeader = (MethodHeader) children.get(0);
    methodBody = (MethodBody) children.get(1);
  }

  public void accept(Visitor v) throws VisitorException {
    v.visit(methodHeader);
    v.visit(methodBody);
    v.visit(this);
  }
}
