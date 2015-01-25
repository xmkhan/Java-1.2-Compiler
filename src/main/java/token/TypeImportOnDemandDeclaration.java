package token;

import java.util.ArrayList;
import visitor.Visitee;
import visitor.Visitor;

public class TypeImportOnDemandDeclaration extends Token implements Visitee {

  public ArrayList<Token> children;

  public TypeImportOnDemandDeclaration(ArrayList<Token> children) {
    super("", TokenType.TypeImportOnDemandDeclaration);
  }

  public void accept(Visitor v) {
    v.visit(this);
  }
}
