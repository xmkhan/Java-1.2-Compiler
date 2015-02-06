package lexer;

import dfa.CommentDFA;
import dfa.DFA;
import dfa.IdentifierDFA;
import dfa.LiteralDFA;
import dfa.NumericDFA;
import dfa.ReservedDFA;
import exception.LexerException;
import token.Token;
import token.TokenType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

/**
 * Defines the Lexer algorithm for parsing the Joos 1W language.
 */
public class Lexer {
  private final DFA[] dfas;
  private HashSet<Character> skipSet;

  public Lexer() {
    // The ordering is preserved by precedence, so that equal length maximal tokens takes the first occurrence.
    dfas = new DFA[] {new CommentDFA(), new ReservedDFA(), new LiteralDFA(), new NumericDFA(), new IdentifierDFA()};
    skipSet = new HashSet<Character>(Arrays.asList(new Character[] {'\n', '\r', ' ', '\t', '\f'}));
    resetDFAs();
  }

  /**
   * Resets all DFAs
   */
  public void resetDFAs() {
    for (DFA dfa : dfas) {
      dfa.reset();
    }
  }

  /**
   * Consumes the input on all DFAs
   * @return false if ALL DFAs failed to consume (all are in error state), true otherwise.
   */
  private boolean consumeDFAs(char c) {
    boolean isConsumed = false;
    for (DFA dfa : dfas) {
      isConsumed |= dfa.consume(c);
    }
    return isConsumed;
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
    int curCharPosition = 1;
    int lineNumber = 1;
    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
    ArrayList<Token> tokens = new ArrayList<Token>();
    char c;
    boolean stop = false;

    for (int input = bufferedReader.read(); !stop ; ) {
      if (input == -1) {
        c = '\n';
        stop = true;
      } else {
        c = (char) input;
      }

      if (c < 0 || c >= 128) {
        throw new LexerException("Error: Out of ASCII range. Occurred at Line#: " + lineNumber +
          "character: " + curCharPosition);
      }

      if (!consumeDFAs(c)) {
        Token maxToken = getMaximalToken();
        if (maxToken != null && !isCommentToken(maxToken)) {
          maxToken.setLocation(lineNumber, curCharPosition - maxToken.getLexeme().length() + 1);
          tokens.add(maxToken);
        }

        if (maxToken == null) {
          if (skipSet.contains(c)) {
            if (c == '\n') {
              lineNumber++;
              curCharPosition = 1;
            } else {
              curCharPosition++;
            }
            input = bufferedReader.read();
          } else {
            throw new LexerException("Error: All DFAs failed to create a valid token. Occurred at line#: " +
              lineNumber + " character: " + curCharPosition);
          }
        }
        // Because the last character led all the DFAs to their error state, reset the DFAs.
        resetDFAs();
      } else {
        if (c == '\n') {
          lineNumber++;
          curCharPosition = 1;
        } else {
          curCharPosition++;
        }
        input = bufferedReader.read();
      }
    }
    tokens.add(new Token("EOF", TokenType.EOF));
    return tokens;
  }

  private boolean isCommentToken(Token token) {
    return token == dfas[0].getToken();
  }
}
