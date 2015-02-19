package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class FormalParameterList extends Token {
  private ArrayList<FormalParameter> formalParameters;

  public ArrayList<FormalParameter> getFormalParameters() {
    return formalParameters;
  }

  public FormalParameterList(ArrayList<Token> children) {
    super("", TokenType.FormalParameterList, children);
    formalParameters = new ArrayList<FormalParameter>();

    if (children.get(0) instanceof FormalParameter) {
      lexeme = children.get(0).getLexeme();
      formalParameters.add((FormalParameter) children.get(0));
    } else {
      FormalParameterList childParameters = (FormalParameterList) children.get(0);
      formalParameters.addAll(childParameters.formalParameters);
      formalParameters.add((FormalParameter) children.get(2));
    }
  }

  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
