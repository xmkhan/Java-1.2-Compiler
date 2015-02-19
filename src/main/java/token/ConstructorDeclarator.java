package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class ConstructorDeclarator extends Token {

  public FormalParameterList paramList;

  public ConstructorDeclarator(ArrayList<Token> children) {
    super(children.get(0).getLexeme(), TokenType.ConstructorDeclarator, children);
    if (children.get(2) instanceof FormalParameterList) {
      paramList = (FormalParameterList) children.get(2);
    }
  }

  public void accept(Visitor v) throws VisitorException {
    if (paramList != null) paramList.accept(v);
    v.visit(this);
  }
}
