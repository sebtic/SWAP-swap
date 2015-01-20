// $ANTLR 2.7.4: "charsetParser.g" -> "EncodingParser.java"$

package info.monitorenter.cpdetector.io.parser;

import antlr.NoViableAltException;
import antlr.ParserSharedInputState;
import antlr.RecognitionException;
import antlr.Token;
import antlr.TokenBuffer;
import antlr.TokenStream;
import antlr.TokenStreamException;
import antlr.collections.impl.BitSet;

public class EncodingParser extends antlr.LLkParser implements EncodingParserTokenTypes {

  public static final String[] _tokenNames = { "<0>", "EOF", "<2>", "NULL_TREE_LOOKAHEAD",
      "META_CONTENT_TYPE", "XML_ENCODING_DECL", "IDENTIFIER", "SPACING", "NEWLINE", "SPACE",
      "DIGIT", "LETTER" };

  public static final BitSet _tokenSet_0 = new BitSet(mk_tokenSet_0());

  private static final long[] mk_tokenSet_0() {
    final long[] data = { 2L, 0L };
    return data;
  }

  public EncodingParser(final ParserSharedInputState state) {
    super(state, 1);
    tokenNames = _tokenNames;
  }

  public EncodingParser(final TokenBuffer tokenBuf) {
    this(tokenBuf, 1);
  }

  protected EncodingParser(final TokenBuffer tokenBuf, final int k) {
    super(tokenBuf, k);
    tokenNames = _tokenNames;
  }

  public EncodingParser(final TokenStream lexer) {
    this(lexer, 1);
  }

  protected EncodingParser(final TokenStream lexer, final int k) {
    super(lexer, k);
    tokenNames = _tokenNames;
  }

  public final String htmlDocument() throws RecognitionException, TokenStreamException {
    String charset;

    Token token1 = null;
    Token token2 = null;
    charset = null;

    try { // for error handling
      switch (LA(1)) {
        case META_CONTENT_TYPE: {
          {
            token1 = LT(1);
            match(META_CONTENT_TYPE);
          }
          charset = token1.getText();
          break;
        }
        case XML_ENCODING_DECL: {
          {
            token2 = LT(1);
            match(XML_ENCODING_DECL);
          }
          charset = token2.getText();
          break;
        }
        case EOF: {
          charset = null;
          break;
        }
        default: {
          throw new NoViableAltException(LT(1), getFilename());
        }
      }
    } catch (final RecognitionException ex) {
      reportError(ex);
      consume();
      consumeUntil(_tokenSet_0);
    }
    return charset;
  }

}
