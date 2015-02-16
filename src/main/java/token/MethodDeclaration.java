package token;

import java.util.ArrayList;
import visitor.Visitor;
import exception.VisitorException;

public class MethodDeclaration extends Token {

  public MethodHeader methodHeader;
  public MethodBody methodBody;

  public MethodDeclaration(ArrayList<Token> children) {
    super("", TokenType.MethodDeclaration, children);
    methodHeader = (MethodHeader) children.get(0);
    methodBody = (MethodBody) children.get(1);
  }

  public void accept(Visitor v) throws VisitorException {
    methodHeader.accept(v);
    methodBody.accept(v);
    v.visit(this);
  }
}
