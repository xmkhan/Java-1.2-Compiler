package lexer;

import dfa.DFA;
import dfa.IdentifierDFA;
import dfa.LiteralDFA;
import dfa.NumericDFA;
import dfa.ReservedDFA;
import token.Token;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Defines the Lexer algorithm for parsing the Joos 1W language.
 */
public class Lexer {

  private final DFA[] dfas;

  public Lexer(IdentifierDFA identifierDFA, LiteralDFA literalDFA, NumericDFA numericDFA, ReservedDFA reservedDFA) {
    dfas = new DFA[] { reservedDFA, literalDFA, numericDFA, identifierDFA };
    resetDFAs();
  }

  /**
   * Resets all DFAs
   */
  private void resetDFAs() {
    for (DFA dfa : dfas) {
      dfa.reset();
    }
  }

  /**
   * Consumes the input on all DFAs
   * @return false if ALL DFAs failed to consume (all are in error state), true otherwise.
   */
  private boolean consumeDFAs(char c) {
    boolean failConsume = true;
    for (DFA dfa : dfas) {
      failConsume &= dfa.consume(c);
    }
    return failConsume;
  }

  /**
   * Gets the largest size token from the DFAs
   */
  private Token getMaximalToken() {
    Token maximalToken = null;
    for (DFA dfa : dfas) {
      Token token = dfa.getToken();
      if (token != null && (maximalToken == null ||
          token.getLexeme().length() > maximalToken.getLexeme().length())) {
        maximalToken = token;
      }
    }
    return maximalToken;
  }


  /**
   * Reads from InputStreamReader, throws an exception on unsupported encoding.
   */
  public ArrayList<Token> parse(InputStreamReader inputStreamReader) throws IOException, LexerException {
    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
    ArrayList<Token> tokens = new ArrayList<>();
    for (int input = bufferedReader.read(); input != -1; ) {
      char c = (char) input;
      if (!consumeDFAs(c)) {
        Token maxToken = getMaximalToken();
        if (maxToken == null) {
          throw new LexerException("All DFAs encountered an error, without a valid token.");
        }
        tokens.add(maxToken);

        // Because the last character led all the DFAs to their error state, re-run
        // the for-loop without incrementing input.
        resetDFAs();
      } else {
        input = bufferedReader.read();
      }
    }
    return tokens;
  }

  public static class LexerException extends Exception {

    public LexerException(String message) {
      super(message);
    }
  }
}
