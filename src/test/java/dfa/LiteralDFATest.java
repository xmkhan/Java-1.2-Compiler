package dfa;

import dfa.LiteralDFA;
import org.junit.Test;
import token.Token;
import token.TokenType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

/**
 * Basic tests to test the Literal DFA.
 */
public class LiteralDFATest {
    @Test
    public void testEmptySingleQuoteLiteral() {
        LiteralDFA dfa = new LiteralDFA();
        String literal = "''";

        for(char c: literal.toCharArray()) {
            dfa.consume(c);
        }
        dfa.consume('\n');

        Token result = dfa.getToken();
        assertEquals(true, result.getTokenType() == TokenType.STR_LITERAL);
        assertEquals(true, result.getLexeme().equals("''"));
    }

    @Test
    public void testEmptyDoubleQuoteLiteral() {
        LiteralDFA dfa = new LiteralDFA();
        String literal = "\"\"";

        for(char c: literal.toCharArray()) {
            dfa.consume(c);
        }
        dfa.consume('\n');

        Token result = dfa.getToken();
        assertEquals(true, result.getTokenType() == TokenType.STR_LITERAL);
        assertEquals(true, result.getLexeme().equals("\"\""));
    }


    @Test
    public void testSingleQuoteInSingleQuotes() {
        LiteralDFA dfa = new LiteralDFA();
        String literal =  "'\\''";

        for(char c: literal.toCharArray()) {
            dfa.consume(c);
        }
        dfa.consume('\n');

        Token result = dfa.getToken();
        assertEquals(true, result.getTokenType() == TokenType.STR_LITERAL);
        assertEquals(true, result.getLexeme().equals("'\\''"));
    }

    @Test
    public void testDoubleQuoteInDoubleQuotes() {
        LiteralDFA dfa = new LiteralDFA();
        String literal =  "\"\\\"\"";

        for(char c: literal.toCharArray()) {
            dfa.consume(c);
        }
        dfa.consume('\n');

        Token result = dfa.getToken();
        assertEquals(true, result.getTokenType() == TokenType.STR_LITERAL);
        assertEquals(true, result.getLexeme().equals("\"\\\"\""));
    }

    @Test
    public void testSingleQuoteInDoubleQuotes() {
        LiteralDFA dfa = new LiteralDFA();
        String literal = "\"\\'\"";

        for(char c: literal.toCharArray()) {
            dfa.consume(c);
        }
        dfa.consume('\n');

        Token result = dfa.getToken();
        assertEquals(true, result.getTokenType() == TokenType.STR_LITERAL);
        assertEquals(true, result.getLexeme().equals("\"\\'\""));
    }

    @Test
    public void testDoubleQuoteInSingleQuotes() {
        LiteralDFA dfa = new LiteralDFA();
        String literal =  "'\\\"'";

        for(char c: literal.toCharArray()) {
            dfa.consume(c);
        }
        dfa.consume('\n');

        Token result = dfa.getToken();
        assertEquals(true, result.getTokenType() == TokenType.STR_LITERAL);
        assertEquals(true, result.getLexeme().equals("'\\\"'"));
    }

    @Test
    public void testLongSingleQuoteLiteral() {
        LiteralDFA dfa = new LiteralDFA();

        dfa.consume('\'');
        dfa.consume('a');
        dfa.consume('b');
        assertEquals(false, dfa.consume('\''));
    }

    @Test
    public void testValidDoubleQuoteLiteral() {
        LiteralDFA dfa = new LiteralDFA();
        String literal =  "\"abc123\\'\\\"\"";

        for(char c: literal.toCharArray()) {
            dfa.consume(c);
        }
        dfa.consume('\n');

        Token result = dfa.getToken();
        assertEquals(true, result.getTokenType() == TokenType.STR_LITERAL);
        assertEquals(true, result.getLexeme().equals("\"abc123\\'\\\"\""));
    }

    @Test
    public void testEscapeCharactersInDoubleQuotes() {
        LiteralDFA dfa = new LiteralDFA();
        String literal =  "\"\\b\\t\\n\\f\\r\\'\\\"\"";

        for(char c: literal.toCharArray()) {
            dfa.consume(c);
        }
        dfa.consume('\n');

        Token result = dfa.getToken();
        assertEquals(true, result.getTokenType() == TokenType.STR_LITERAL);
        assertEquals(true, result.getLexeme().equals("\"\\b\\t\\n\\f\\r\\'\\\"\""));
    }

    @Test
    public void testEscapeHexInDoubleQuotes() {
        LiteralDFA dfa = new LiteralDFA();
        String literal =  "\"\\377a\"";

        for(char c: literal.toCharArray()) {
            dfa.consume(c);
        }
        dfa.consume('\n');

        Token result = dfa.getToken();
        assertEquals(true, result.getTokenType() == TokenType.STR_LITERAL);
        assertEquals(true, result.getLexeme().equals("\"\\377a\""));
    }

    @Test
    public void testEscapeCharacterInSingleQuotes() {
        LiteralDFA dfa = new LiteralDFA();
        String literal =  "'\t'";

        for(char c: literal.toCharArray()) {
            dfa.consume(c);
        }
        dfa.consume('\n');

        Token result = dfa.getToken();
        assertEquals(true, result.getTokenType() == TokenType.STR_LITERAL);
        assertEquals(true, result.getLexeme().equals("'\t'"));
    }

    @Test
    public void testEscapeHexInSingleQuotes() {
        LiteralDFA dfa = new LiteralDFA();
        String literal =  "'\\377'";

        for(char c: literal.toCharArray()) {
            dfa.consume(c);
        }
        dfa.consume('\n');

        Token result = dfa.getToken();
        assertEquals(true, result.getTokenType() == TokenType.STR_LITERAL);
        assertEquals(true, result.getLexeme().equals("'\\377'"));
    }

    @Test
    public void testHexEscapeOutOfRange() {
        LiteralDFA dfa = new LiteralDFA();
        String literal =  "'\\378'";

        dfa.consume('\'');
        dfa.consume('\\');
        dfa.consume('3');
        dfa.consume('7');
        dfa.consume('8');
        assertEquals(false, dfa.consume('\''));

        dfa.reset();
        dfa.consume('\'');
        dfa.consume('\\');
        dfa.consume('3');
        dfa.consume('8');
        dfa.consume('7');
        assertEquals(false, dfa.consume('\''));

        dfa.reset();
        dfa.consume('\'');
        dfa.consume('\\');
        dfa.consume('4');
        dfa.consume('7');
        dfa.consume('7');
        assertEquals(false, dfa.consume('\''));
    }
}