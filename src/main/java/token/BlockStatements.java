package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;
import java.util.List;

public class BlockStatements extends BaseStatement {

  public ArrayList<BlockStatement> blockStatements;

  public ArrayList<BlockStatement> getStatements() {
    return blockStatements;
  }

  public BlockStatements(ArrayList<Token> children) {
    super("", TokenType.BlockStatements, children);
    blockStatements = new ArrayList<BlockStatement>();
    if (children.get(0) instanceof BlockStatement) {
      lexeme = children.get(0).getLexeme();
      blockStatements.add((BlockStatement) children.get(0));
    } else {
      BlockStatements childBlockStatements = (BlockStatements) children.get(0);
      blockStatements.addAll(childBlockStatements.blockStatements);
      blockStatements.add((BlockStatement) children.get(1));
    }
  }

  @Override
  public void accept(Visitor v) throws VisitorException {
    for (BlockStatement token : blockStatements) {
      token.accept(v);
    }
    v.visit(this);
  }

  @Override
  public void acceptReverse(Visitor v) throws VisitorException {
    v.visit(this);
    for (BlockStatement token : blockStatements) {
      token.acceptReverse(v);
    }
  }

  @Override
  public void traverse(Visitor v) throws VisitorException {
    v.visit(this);
  }
}
