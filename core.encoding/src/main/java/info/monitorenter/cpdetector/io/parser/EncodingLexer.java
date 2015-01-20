// $ANTLR 2.7.4: "charsetParser.g" -> "EncodingLexer.java"$

package info.monitorenter.cpdetector.io.parser;

import java.io.InputStream;
import java.io.Reader;
import java.util.Hashtable;
import antlr.ByteBuffer;
import antlr.CharBuffer;
import antlr.CharStreamException;
import antlr.CharStreamIOException;
import antlr.InputBuffer;
import antlr.LexerSharedInputState;
import antlr.NoViableAltForCharException;
import antlr.RecognitionException;
import antlr.Token;
import antlr.TokenStream;
import antlr.TokenStreamException;
import antlr.TokenStreamIOException;
import antlr.TokenStreamRecognitionException;
import antlr.collections.impl.BitSet;

public class EncodingLexer extends antlr.CharScanner implements EncodingParserTokenTypes,
    TokenStream {
  public static final BitSet _tokenSet_0 = new BitSet(mk_tokenSet_0());

  private static final long[] mk_tokenSet_0() {
    final long[] data = new long[1025];
    data[0] = 4294976512L;
    data[1] = 35184372088832L;
    return data;
  }

  public EncodingLexer(final InputBuffer ib) {
    this(new LexerSharedInputState(ib));
  }

  public EncodingLexer(final InputStream in) {
    this(new ByteBuffer(in));
  }

  @SuppressWarnings("rawtypes")
  public EncodingLexer(final LexerSharedInputState state) {
    super(state);
    caseSensitiveLiterals = true;
    setCaseSensitive(false);
    literals = new Hashtable();
  }

  public EncodingLexer(final Reader in) {
    this(new CharBuffer(in));
  }

  protected final void mDIGIT(final boolean _createToken) throws RecognitionException,
      CharStreamException, TokenStreamException {
    int _ttype;
    Token _token = null;
    final int _begin = text.length();
    _ttype = DIGIT;
    matchRange('0', '9');
    if (_createToken && _token == null && _ttype != Token.SKIP) {
      _token = makeToken(_ttype);
      _token.setText(new String(text.getBuffer(), _begin, text.length() - _begin));
    }
    _returnToken = _token;
  }

  protected final void mIDENTIFIER(final boolean _createToken) throws RecognitionException,
      CharStreamException, TokenStreamException {
    int _ttype;
    Token _token = null;
    final int _begin = text.length();
    _ttype = IDENTIFIER;
    mLETTER(false);
    {
      _loop61: do {
        switch (LA(1)) {
          case 'a':
          case 'b':
          case 'c':
          case 'd':
          case 'e':
          case 'f':
          case 'g':
          case 'h':
          case 'i':
          case 'j':
          case 'k':
          case 'l':
          case 'm':
          case 'n':
          case 'o':
          case 'p':
          case 'q':
          case 'r':
          case 's':
          case 't':
          case 'u':
          case 'v':
          case 'w':
          case 'x':
          case 'y':
          case 'z': {
            mLETTER(false);
            break;
          }
          case '0':
          case '1':
          case '2':
          case '3':
          case '4':
          case '5':
          case '6':
          case '7':
          case '8':
          case '9': {
            mDIGIT(false);
            break;
          }
          case '_': {
            match('_');
            break;
          }
          case '.': {
            match('.');
            break;
          }
          case '-': {
            match('-');
            break;
          }
          default: {
            break _loop61;
          }
        }
      } while (true);
    }
    if (_createToken && _token == null && _ttype != Token.SKIP) {
      _token = makeToken(_ttype);
      _token.setText(new String(text.getBuffer(), _begin, text.length() - _begin));
    }
    _returnToken = _token;
  }

  protected final void mLETTER(final boolean _createToken) throws RecognitionException,
      CharStreamException, TokenStreamException {
    int _ttype;
    Token _token = null;
    final int _begin = text.length();
    _ttype = LETTER;
    matchRange('a', 'z');
    if (_createToken && _token == null && _ttype != Token.SKIP) {
      _token = makeToken(_ttype);
      _token.setText(new String(text.getBuffer(), _begin, text.length() - _begin));
    }
    _returnToken = _token;
  }

  public final void mMETA_CONTENT_TYPE(final boolean _createToken) throws RecognitionException,
      CharStreamException, TokenStreamException {
    int _ttype;
    Token _token = null;
    final int _begin = text.length();
    _ttype = META_CONTENT_TYPE;
    int _saveIndex;

    _saveIndex = text.length();
    match('<');
    text.setLength(_saveIndex);
    {
      switch (LA(1)) {
        case '\n':
        case '\r':
        case ' ': {
          _saveIndex = text.length();
          mSPACING(false);
          text.setLength(_saveIndex);
          break;
        }
        case 'm': {
          break;
        }
        default: {
          throw new NoViableAltForCharException(LA(1), getFilename(), getLine(), getColumn());
        }
      }
    }
    _saveIndex = text.length();
    match("meta");
    text.setLength(_saveIndex);
    {
      switch (LA(1)) {
        case '\n':
        case '\r':
        case ' ': {
          _saveIndex = text.length();
          mSPACING(false);
          text.setLength(_saveIndex);
          break;
        }
        case 'h': {
          break;
        }
        default: {
          throw new NoViableAltForCharException(LA(1), getFilename(), getLine(), getColumn());
        }
      }
    }
    _saveIndex = text.length();
    match("http-equiv");
    text.setLength(_saveIndex);
    {
      switch (LA(1)) {
        case '\n':
        case '\r':
        case ' ': {
          _saveIndex = text.length();
          mSPACING(false);
          text.setLength(_saveIndex);
          break;
        }
        case '=': {
          break;
        }
        default: {
          throw new NoViableAltForCharException(LA(1), getFilename(), getLine(), getColumn());
        }
      }
    }
    _saveIndex = text.length();
    match('=');
    text.setLength(_saveIndex);
    {
      switch (LA(1)) {
        case '\n':
        case '\r':
        case ' ': {
          _saveIndex = text.length();
          mSPACING(false);
          text.setLength(_saveIndex);
          break;
        }
        case '"':
        case 'c': {
          break;
        }
        default: {
          throw new NoViableAltForCharException(LA(1), getFilename(), getLine(), getColumn());
        }
      }
    }
    {
      switch (LA(1)) {
        case '"': {
          _saveIndex = text.length();
          match('\"');
          text.setLength(_saveIndex);
          {
            switch (LA(1)) {
              case '\n':
              case '\r':
              case ' ': {
                _saveIndex = text.length();
                mSPACING(false);
                text.setLength(_saveIndex);
                break;
              }
              case 'c': {
                break;
              }
              default: {
                throw new NoViableAltForCharException(LA(1), getFilename(), getLine(), getColumn());
              }
            }
          }
          break;
        }
        case 'c': {
          break;
        }
        default: {
          throw new NoViableAltForCharException(LA(1), getFilename(), getLine(), getColumn());
        }
      }
    }
    _saveIndex = text.length();
    match("content-type");
    text.setLength(_saveIndex);
    {
      switch (LA(1)) {
        case '\n':
        case '\r':
        case ' ': {
          _saveIndex = text.length();
          mSPACING(false);
          text.setLength(_saveIndex);
          break;
        }
        case '"':
        case 'c': {
          break;
        }
        default: {
          throw new NoViableAltForCharException(LA(1), getFilename(), getLine(), getColumn());
        }
      }
    }
    {
      switch (LA(1)) {
        case '"': {
          _saveIndex = text.length();
          match('\"');
          text.setLength(_saveIndex);
          {
            switch (LA(1)) {
              case '\n':
              case '\r':
              case ' ': {
                _saveIndex = text.length();
                mSPACING(false);
                text.setLength(_saveIndex);
                break;
              }
              case 'c': {
                break;
              }
              default: {
                throw new NoViableAltForCharException(LA(1), getFilename(), getLine(), getColumn());
              }
            }
          }
          break;
        }
        case 'c': {
          break;
        }
        default: {
          throw new NoViableAltForCharException(LA(1), getFilename(), getLine(), getColumn());
        }
      }
    }
    _saveIndex = text.length();
    match("content");
    text.setLength(_saveIndex);
    {
      switch (LA(1)) {
        case '\n':
        case '\r':
        case ' ': {
          _saveIndex = text.length();
          mSPACING(false);
          text.setLength(_saveIndex);
          break;
        }
        case '=': {
          break;
        }
        default: {
          throw new NoViableAltForCharException(LA(1), getFilename(), getLine(), getColumn());
        }
      }
    }
    _saveIndex = text.length();
    match('=');
    text.setLength(_saveIndex);
    {
      switch (LA(1)) {
        case '\n':
        case '\r':
        case ' ': {
          _saveIndex = text.length();
          mSPACING(false);
          text.setLength(_saveIndex);
          break;
        }
        case '"':
        case '/':
        case '0':
        case '1':
        case '2':
        case '3':
        case '4':
        case '5':
        case '6':
        case '7':
        case '8':
        case '9':
        case ';':
        case 'a':
        case 'b':
        case 'c':
        case 'd':
        case 'e':
        case 'f':
        case 'g':
        case 'h':
        case 'i':
        case 'j':
        case 'k':
        case 'l':
        case 'm':
        case 'n':
        case 'o':
        case 'p':
        case 'q':
        case 'r':
        case 's':
        case 't':
        case 'u':
        case 'v':
        case 'w':
        case 'x':
        case 'y':
        case 'z': {
          break;
        }
        default: {
          throw new NoViableAltForCharException(LA(1), getFilename(), getLine(), getColumn());
        }
      }
    }
    {
      switch (LA(1)) {
        case '"': {
          _saveIndex = text.length();
          match('\"');
          text.setLength(_saveIndex);
          {
            switch (LA(1)) {
              case '\n':
              case '\r':
              case ' ': {
                _saveIndex = text.length();
                mSPACING(false);
                text.setLength(_saveIndex);
                break;
              }
              case '/':
              case '0':
              case '1':
              case '2':
              case '3':
              case '4':
              case '5':
              case '6':
              case '7':
              case '8':
              case '9':
              case ';':
              case 'a':
              case 'b':
              case 'c':
              case 'd':
              case 'e':
              case 'f':
              case 'g':
              case 'h':
              case 'i':
              case 'j':
              case 'k':
              case 'l':
              case 'm':
              case 'n':
              case 'o':
              case 'p':
              case 'q':
              case 'r':
              case 's':
              case 't':
              case 'u':
              case 'v':
              case 'w':
              case 'x':
              case 'y':
              case 'z': {
                break;
              }
              default: {
                throw new NoViableAltForCharException(LA(1), getFilename(), getLine(), getColumn());
              }
            }
          }
          break;
        }
        case '/':
        case '0':
        case '1':
        case '2':
        case '3':
        case '4':
        case '5':
        case '6':
        case '7':
        case '8':
        case '9':
        case ';':
        case 'a':
        case 'b':
        case 'c':
        case 'd':
        case 'e':
        case 'f':
        case 'g':
        case 'h':
        case 'i':
        case 'j':
        case 'k':
        case 'l':
        case 'm':
        case 'n':
        case 'o':
        case 'p':
        case 'q':
        case 'r':
        case 's':
        case 't':
        case 'u':
        case 'v':
        case 'w':
        case 'x':
        case 'y':
        case 'z': {
          break;
        }
        default: {
          throw new NoViableAltForCharException(LA(1), getFilename(), getLine(), getColumn());
        }
      }
    }
    {
      _loop22: do {
        switch (LA(1)) {
          case 'a':
          case 'b':
          case 'c':
          case 'd':
          case 'e':
          case 'f':
          case 'g':
          case 'h':
          case 'i':
          case 'j':
          case 'k':
          case 'l':
          case 'm':
          case 'n':
          case 'o':
          case 'p':
          case 'q':
          case 'r':
          case 's':
          case 't':
          case 'u':
          case 'v':
          case 'w':
          case 'x':
          case 'y':
          case 'z': {
            _saveIndex = text.length();
            mLETTER(false);
            text.setLength(_saveIndex);
            {
              switch (LA(1)) {
                case '\n':
                case '\r':
                case ' ': {
                  _saveIndex = text.length();
                  mSPACING(false);
                  text.setLength(_saveIndex);
                  break;
                }
                case '/':
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                case ';':
                case 'a':
                case 'b':
                case 'c':
                case 'd':
                case 'e':
                case 'f':
                case 'g':
                case 'h':
                case 'i':
                case 'j':
                case 'k':
                case 'l':
                case 'm':
                case 'n':
                case 'o':
                case 'p':
                case 'q':
                case 'r':
                case 's':
                case 't':
                case 'u':
                case 'v':
                case 'w':
                case 'x':
                case 'y':
                case 'z': {
                  break;
                }
                default: {
                  throw new NoViableAltForCharException(LA(1), getFilename(), getLine(),
                      getColumn());
                }
              }
            }
            break;
          }
          case '0':
          case '1':
          case '2':
          case '3':
          case '4':
          case '5':
          case '6':
          case '7':
          case '8':
          case '9': {
            _saveIndex = text.length();
            mDIGIT(false);
            text.setLength(_saveIndex);
            {
              switch (LA(1)) {
                case '\n':
                case '\r':
                case ' ': {
                  _saveIndex = text.length();
                  mSPACING(false);
                  text.setLength(_saveIndex);
                  break;
                }
                case '/':
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                case ';':
                case 'a':
                case 'b':
                case 'c':
                case 'd':
                case 'e':
                case 'f':
                case 'g':
                case 'h':
                case 'i':
                case 'j':
                case 'k':
                case 'l':
                case 'm':
                case 'n':
                case 'o':
                case 'p':
                case 'q':
                case 'r':
                case 's':
                case 't':
                case 'u':
                case 'v':
                case 'w':
                case 'x':
                case 'y':
                case 'z': {
                  break;
                }
                default: {
                  throw new NoViableAltForCharException(LA(1), getFilename(), getLine(),
                      getColumn());
                }
              }
            }
            break;
          }
          case '/': {
            _saveIndex = text.length();
            match('/');
            text.setLength(_saveIndex);
            {
              switch (LA(1)) {
                case '\n':
                case '\r':
                case ' ': {
                  _saveIndex = text.length();
                  mSPACING(false);
                  text.setLength(_saveIndex);
                  break;
                }
                case '/':
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                case ';':
                case 'a':
                case 'b':
                case 'c':
                case 'd':
                case 'e':
                case 'f':
                case 'g':
                case 'h':
                case 'i':
                case 'j':
                case 'k':
                case 'l':
                case 'm':
                case 'n':
                case 'o':
                case 'p':
                case 'q':
                case 'r':
                case 's':
                case 't':
                case 'u':
                case 'v':
                case 'w':
                case 'x':
                case 'y':
                case 'z': {
                  break;
                }
                default: {
                  throw new NoViableAltForCharException(LA(1), getFilename(), getLine(),
                      getColumn());
                }
              }
            }
            break;
          }
          default: {
            break _loop22;
          }
        }
      } while (true);
    }
    _saveIndex = text.length();
    match(';');
    text.setLength(_saveIndex);
    {
      switch (LA(1)) {
        case '\n':
        case '\r':
        case ' ': {
          _saveIndex = text.length();
          mSPACING(false);
          text.setLength(_saveIndex);
          break;
        }
        case 'c': {
          break;
        }
        default: {
          throw new NoViableAltForCharException(LA(1), getFilename(), getLine(), getColumn());
        }
      }
    }
    _saveIndex = text.length();
    match("charset");
    text.setLength(_saveIndex);
    {
      switch (LA(1)) {
        case '\n':
        case '\r':
        case ' ': {
          _saveIndex = text.length();
          mSPACING(false);
          text.setLength(_saveIndex);
          break;
        }
        case '=': {
          break;
        }
        default: {
          throw new NoViableAltForCharException(LA(1), getFilename(), getLine(), getColumn());
        }
      }
    }
    _saveIndex = text.length();
    match('=');
    text.setLength(_saveIndex);
    {
      if ((LA(1) == '\n' || LA(1) == '\r' || LA(1) == ' ')) {
        _saveIndex = text.length();
        mSPACING(false);
        text.setLength(_saveIndex);
      } else {
      }

    }
    {
      _loop31: do {
        switch (LA(1)) {
          case 'a':
          case 'b':
          case 'c':
          case 'd':
          case 'e':
          case 'f':
          case 'g':
          case 'h':
          case 'i':
          case 'j':
          case 'k':
          case 'l':
          case 'm':
          case 'n':
          case 'o':
          case 'p':
          case 'q':
          case 'r':
          case 's':
          case 't':
          case 'u':
          case 'v':
          case 'w':
          case 'x':
          case 'y':
          case 'z': {
            mLETTER(false);
            {
              if ((LA(1) == '\n' || LA(1) == '\r' || LA(1) == ' ')) {
                _saveIndex = text.length();
                mSPACING(false);
                text.setLength(_saveIndex);
              } else {
              }

            }
            break;
          }
          case '0':
          case '1':
          case '2':
          case '3':
          case '4':
          case '5':
          case '6':
          case '7':
          case '8':
          case '9': {
            mDIGIT(false);
            {
              if ((LA(1) == '\n' || LA(1) == '\r' || LA(1) == ' ')) {
                _saveIndex = text.length();
                mSPACING(false);
                text.setLength(_saveIndex);
              } else {
              }

            }
            break;
          }
          case '-': {
            match('-');
            {
              if ((LA(1) == '\n' || LA(1) == '\r' || LA(1) == ' ')) {
                _saveIndex = text.length();
                mSPACING(false);
                text.setLength(_saveIndex);
              } else {
              }

            }
            break;
          }
          case '_': {
            match('_');
            {
              if ((LA(1) == '\n' || LA(1) == '\r' || LA(1) == ' ')) {
                _saveIndex = text.length();
                mSPACING(false);
                text.setLength(_saveIndex);
              } else {
              }

            }
            break;
          }
          default: {
            break _loop31;
          }
        }
      } while (true);
    }
    {
      if ((LA(1) == '"')) {
        _saveIndex = text.length();
        match('\"');
        text.setLength(_saveIndex);
        {
          if ((LA(1) == '\n' || LA(1) == '\r' || LA(1) == ' ')) {
            _saveIndex = text.length();
            mSPACING(false);
            text.setLength(_saveIndex);
          } else {
          }

        }
      } else {
      }

    }
    if (_createToken && _token == null && _ttype != Token.SKIP) {
      _token = makeToken(_ttype);
      _token.setText(new String(text.getBuffer(), _begin, text.length() - _begin));
    }
    _returnToken = _token;
  }

  protected final void mNEWLINE(final boolean _createToken) throws RecognitionException,
      CharStreamException, TokenStreamException {
    int _ttype;
    Token _token = null;
    final int _begin = text.length();
    _ttype = NEWLINE;
    switch (LA(1)) {
      case '\n': {
        match('\n');
        newline();
        break;
      }
      case '\r': {
        match('\r');
        match('\n');
        newline();
        break;
      }
      default: {
        throw new NoViableAltForCharException(LA(1), getFilename(), getLine(), getColumn());
      }
    }
    if (_createToken && _token == null && _ttype != Token.SKIP) {
      _token = makeToken(_ttype);
      _token.setText(new String(text.getBuffer(), _begin, text.length() - _begin));
    }
    _returnToken = _token;
  }

  protected final void mSPACE(final boolean _createToken) throws RecognitionException,
      CharStreamException, TokenStreamException {
    int _ttype;
    Token _token = null;
    final int _begin = text.length();
    _ttype = SPACE;
    match(' ');
    if (_createToken && _token == null && _ttype != Token.SKIP) {
      _token = makeToken(_ttype);
      _token.setText(new String(text.getBuffer(), _begin, text.length() - _begin));
    }
    _returnToken = _token;
  }

  protected final void mSPACING(final boolean _createToken) throws RecognitionException,
      CharStreamException, TokenStreamException {
    int _ttype;
    Token _token = null;
    final int _begin = text.length();
    _ttype = SPACING;
    switch (LA(1)) {
      case '\n':
      case '\r': {
        mNEWLINE(false);
        break;
      }
      case ' ': {
        mSPACE(false);
        break;
      }
      default: {
        throw new NoViableAltForCharException(LA(1), getFilename(), getLine(), getColumn());
      }
    }
    if (_createToken && _token == null && _ttype != Token.SKIP) {
      _token = makeToken(_ttype);
      _token.setText(new String(text.getBuffer(), _begin, text.length() - _begin));
    }
    _returnToken = _token;
  }

  public final void mXML_ENCODING_DECL(final boolean _createToken) throws RecognitionException,
      CharStreamException, TokenStreamException {
    int _ttype;
    Token _token = null;
    final int _begin = text.length();
    _ttype = XML_ENCODING_DECL;
    int _saveIndex;

    _saveIndex = text.length();
    match("<?xml");
    text.setLength(_saveIndex);
    {
      switch (LA(1)) {
        case '\n':
        case '\r':
        case ' ': {
          _saveIndex = text.length();
          mSPACING(false);
          text.setLength(_saveIndex);
          break;
        }
        case 'e':
        case 'v': {
          break;
        }
        default: {
          throw new NoViableAltForCharException(LA(1), getFilename(), getLine(), getColumn());
        }
      }
    }
    {
      switch (LA(1)) {
        case 'v': {
          _saveIndex = text.length();
          match("version");
          text.setLength(_saveIndex);
          {
            switch (LA(1)) {
              case '\n':
              case '\r':
              case ' ': {
                _saveIndex = text.length();
                mSPACING(false);
                text.setLength(_saveIndex);
                break;
              }
              case '=': {
                break;
              }
              default: {
                throw new NoViableAltForCharException(LA(1), getFilename(), getLine(), getColumn());
              }
            }
          }
          _saveIndex = text.length();
          match("=");
          text.setLength(_saveIndex);
          {
            switch (LA(1)) {
              case '\n':
              case '\r':
              case ' ': {
                _saveIndex = text.length();
                mSPACING(false);
                text.setLength(_saveIndex);
                break;
              }
              case '"':
              case '\'': {
                break;
              }
              default: {
                throw new NoViableAltForCharException(LA(1), getFilename(), getLine(), getColumn());
              }
            }
          }
          {
            switch (LA(1)) {
              case '\'': {
                _saveIndex = text.length();
                match("'");
                text.setLength(_saveIndex);
                {
                  switch (LA(1)) {
                    case '\n':
                    case '\r':
                    case ' ': {
                      _saveIndex = text.length();
                      mSPACING(false);
                      text.setLength(_saveIndex);
                      break;
                    }
                    case '0':
                    case '1':
                    case '2':
                    case '3':
                    case '4':
                    case '5':
                    case '6':
                    case '7':
                    case '8':
                    case '9': {
                      break;
                    }
                    default: {
                      throw new NoViableAltForCharException(LA(1), getFilename(), getLine(),
                          getColumn());
                    }
                  }
                }
                _saveIndex = text.length();
                mDIGIT(false);
                text.setLength(_saveIndex);
                {
                  switch (LA(1)) {
                    case '\n':
                    case '\r':
                    case ' ': {
                      _saveIndex = text.length();
                      mSPACING(false);
                      text.setLength(_saveIndex);
                      break;
                    }
                    case '.': {
                      break;
                    }
                    default: {
                      throw new NoViableAltForCharException(LA(1), getFilename(), getLine(),
                          getColumn());
                    }
                  }
                }
                _saveIndex = text.length();
                match('.');
                text.setLength(_saveIndex);
                {
                  switch (LA(1)) {
                    case '\n':
                    case '\r':
                    case ' ': {
                      _saveIndex = text.length();
                      mSPACING(false);
                      text.setLength(_saveIndex);
                      break;
                    }
                    case '0':
                    case '1':
                    case '2':
                    case '3':
                    case '4':
                    case '5':
                    case '6':
                    case '7':
                    case '8':
                    case '9': {
                      break;
                    }
                    default: {
                      throw new NoViableAltForCharException(LA(1), getFilename(), getLine(),
                          getColumn());
                    }
                  }
                }
                _saveIndex = text.length();
                mDIGIT(false);
                text.setLength(_saveIndex);
                {
                  switch (LA(1)) {
                    case '\n':
                    case '\r':
                    case ' ': {
                      _saveIndex = text.length();
                      mSPACING(false);
                      text.setLength(_saveIndex);
                      break;
                    }
                    case '\'': {
                      break;
                    }
                    default: {
                      throw new NoViableAltForCharException(LA(1), getFilename(), getLine(),
                          getColumn());
                    }
                  }
                }
                _saveIndex = text.length();
                match("'");
                text.setLength(_saveIndex);
                {
                  switch (LA(1)) {
                    case '\n':
                    case '\r':
                    case ' ': {
                      _saveIndex = text.length();
                      mSPACING(false);
                      text.setLength(_saveIndex);
                      break;
                    }
                    case 'e': {
                      break;
                    }
                    default: {
                      throw new NoViableAltForCharException(LA(1), getFilename(), getLine(),
                          getColumn());
                    }
                  }
                }
                break;
              }
              case '"': {
                _saveIndex = text.length();
                match('"');
                text.setLength(_saveIndex);
                {
                  switch (LA(1)) {
                    case '\n':
                    case '\r':
                    case ' ': {
                      _saveIndex = text.length();
                      mSPACING(false);
                      text.setLength(_saveIndex);
                      break;
                    }
                    case '0':
                    case '1':
                    case '2':
                    case '3':
                    case '4':
                    case '5':
                    case '6':
                    case '7':
                    case '8':
                    case '9': {
                      break;
                    }
                    default: {
                      throw new NoViableAltForCharException(LA(1), getFilename(), getLine(),
                          getColumn());
                    }
                  }
                }
                _saveIndex = text.length();
                mDIGIT(false);
                text.setLength(_saveIndex);
                {
                  switch (LA(1)) {
                    case '\n':
                    case '\r':
                    case ' ': {
                      _saveIndex = text.length();
                      mSPACING(false);
                      text.setLength(_saveIndex);
                      break;
                    }
                    case '.': {
                      break;
                    }
                    default: {
                      throw new NoViableAltForCharException(LA(1), getFilename(), getLine(),
                          getColumn());
                    }
                  }
                }
                _saveIndex = text.length();
                match('.');
                text.setLength(_saveIndex);
                {
                  switch (LA(1)) {
                    case '\n':
                    case '\r':
                    case ' ': {
                      _saveIndex = text.length();
                      mSPACING(false);
                      text.setLength(_saveIndex);
                      break;
                    }
                    case '0':
                    case '1':
                    case '2':
                    case '3':
                    case '4':
                    case '5':
                    case '6':
                    case '7':
                    case '8':
                    case '9': {
                      break;
                    }
                    default: {
                      throw new NoViableAltForCharException(LA(1), getFilename(), getLine(),
                          getColumn());
                    }
                  }
                }
                _saveIndex = text.length();
                mDIGIT(false);
                text.setLength(_saveIndex);
                {
                  switch (LA(1)) {
                    case '\n':
                    case '\r':
                    case ' ': {
                      _saveIndex = text.length();
                      mSPACING(false);
                      text.setLength(_saveIndex);
                      break;
                    }
                    case '"': {
                      break;
                    }
                    default: {
                      throw new NoViableAltForCharException(LA(1), getFilename(), getLine(),
                          getColumn());
                    }
                  }
                }
                _saveIndex = text.length();
                match('"');
                text.setLength(_saveIndex);
                {
                  switch (LA(1)) {
                    case '\n':
                    case '\r':
                    case ' ': {
                      _saveIndex = text.length();
                      mSPACING(false);
                      text.setLength(_saveIndex);
                      break;
                    }
                    case 'e': {
                      break;
                    }
                    default: {
                      throw new NoViableAltForCharException(LA(1), getFilename(), getLine(),
                          getColumn());
                    }
                  }
                }
                break;
              }
              default: {
                throw new NoViableAltForCharException(LA(1), getFilename(), getLine(), getColumn());
              }
            }
          }
          break;
        }
        case 'e': {
          break;
        }
        default: {
          throw new NoViableAltForCharException(LA(1), getFilename(), getLine(), getColumn());
        }
      }
    }
    _saveIndex = text.length();
    match("encoding");
    text.setLength(_saveIndex);
    {
      switch (LA(1)) {
        case '\n':
        case '\r':
        case ' ': {
          _saveIndex = text.length();
          mSPACING(false);
          text.setLength(_saveIndex);
          break;
        }
        case '=': {
          break;
        }
        default: {
          throw new NoViableAltForCharException(LA(1), getFilename(), getLine(), getColumn());
        }
      }
    }
    _saveIndex = text.length();
    match("=");
    text.setLength(_saveIndex);
    {
      switch (LA(1)) {
        case '\n':
        case '\r':
        case ' ': {
          _saveIndex = text.length();
          mSPACING(false);
          text.setLength(_saveIndex);
          break;
        }
        case '"':
        case '\'': {
          break;
        }
        default: {
          throw new NoViableAltForCharException(LA(1), getFilename(), getLine(), getColumn());
        }
      }
    }
    {
      switch (LA(1)) {
        case '\'': {
          _saveIndex = text.length();
          match("'");
          text.setLength(_saveIndex);
          {
            switch (LA(1)) {
              case '\n':
              case '\r':
              case ' ': {
                _saveIndex = text.length();
                mSPACING(false);
                text.setLength(_saveIndex);
                break;
              }
              case 'a':
              case 'b':
              case 'c':
              case 'd':
              case 'e':
              case 'f':
              case 'g':
              case 'h':
              case 'i':
              case 'j':
              case 'k':
              case 'l':
              case 'm':
              case 'n':
              case 'o':
              case 'p':
              case 'q':
              case 'r':
              case 's':
              case 't':
              case 'u':
              case 'v':
              case 'w':
              case 'x':
              case 'y':
              case 'z': {
                break;
              }
              default: {
                throw new NoViableAltForCharException(LA(1), getFilename(), getLine(), getColumn());
              }
            }
          }
          mIDENTIFIER(false);
          {
            switch (LA(1)) {
              case '\n':
              case '\r':
              case ' ': {
                _saveIndex = text.length();
                mSPACING(false);
                text.setLength(_saveIndex);
                break;
              }
              case '\'': {
                break;
              }
              default: {
                throw new NoViableAltForCharException(LA(1), getFilename(), getLine(), getColumn());
              }
            }
          }
          _saveIndex = text.length();
          match("'");
          text.setLength(_saveIndex);
          {
            if ((LA(1) == '\n' || LA(1) == '\r' || LA(1) == ' ')) {
              _saveIndex = text.length();
              mSPACING(false);
              text.setLength(_saveIndex);
            } else {
            }

          }
          break;
        }
        case '"': {
          _saveIndex = text.length();
          match('"');
          text.setLength(_saveIndex);
          {
            switch (LA(1)) {
              case '\n':
              case '\r':
              case ' ': {
                _saveIndex = text.length();
                mSPACING(false);
                text.setLength(_saveIndex);
                break;
              }
              case 'a':
              case 'b':
              case 'c':
              case 'd':
              case 'e':
              case 'f':
              case 'g':
              case 'h':
              case 'i':
              case 'j':
              case 'k':
              case 'l':
              case 'm':
              case 'n':
              case 'o':
              case 'p':
              case 'q':
              case 'r':
              case 's':
              case 't':
              case 'u':
              case 'v':
              case 'w':
              case 'x':
              case 'y':
              case 'z': {
                break;
              }
              default: {
                throw new NoViableAltForCharException(LA(1), getFilename(), getLine(), getColumn());
              }
            }
          }
          mIDENTIFIER(false);
          {
            switch (LA(1)) {
              case '\n':
              case '\r':
              case ' ': {
                _saveIndex = text.length();
                mSPACING(false);
                text.setLength(_saveIndex);
                break;
              }
              case '"': {
                break;
              }
              default: {
                throw new NoViableAltForCharException(LA(1), getFilename(), getLine(), getColumn());
              }
            }
          }
          _saveIndex = text.length();
          match('"');
          text.setLength(_saveIndex);
          {
            if ((LA(1) == '\n' || LA(1) == '\r' || LA(1) == ' ')) {
              _saveIndex = text.length();
              mSPACING(false);
              text.setLength(_saveIndex);
            } else {
            }

          }
          break;
        }
        default: {
          throw new NoViableAltForCharException(LA(1), getFilename(), getLine(), getColumn());
        }
      }
    }
    if (_createToken && _token == null && _ttype != Token.SKIP) {
      _token = makeToken(_ttype);
      _token.setText(new String(text.getBuffer(), _begin, text.length() - _begin));
    }
    _returnToken = _token;
  }

  @Override
  public Token nextToken() throws TokenStreamException {
    tryAgain: for (;;) {
      int _ttype = Token.INVALID_TYPE;
      setCommitToPath(false);
      resetText();
      try { // for char stream error handling
        try { // for lexical error handling
          if ((LA(1) == '<') && (_tokenSet_0.member(LA(2)))) {
            mMETA_CONTENT_TYPE(true);
          } else if ((LA(1) == '<') && (LA(2) == '?')) {
            mXML_ENCODING_DECL(true);
          } else {
            if (LA(1) == EOF_CHAR) {
              uponEOF();
              _returnToken = makeToken(Token.EOF_TYPE);
            } else {
              consume();
              continue tryAgain;
            }
          }

          if (_returnToken == null) {
            continue tryAgain; // found SKIP token
          }
          _ttype = _returnToken.getType();
          _ttype = testLiteralsTable(_ttype);
          _returnToken.setType(_ttype);
          return _returnToken;
        } catch (final RecognitionException e) {
          if (!getCommitToPath()) {
            consume();
            continue tryAgain;
          }
          throw new TokenStreamRecognitionException(e);
        }
      } catch (final CharStreamException cse) {
        if (cse instanceof CharStreamIOException) {
          throw new TokenStreamIOException(((CharStreamIOException) cse).io);
        } else {
          throw new TokenStreamException(cse.getMessage());
        }
      }
    }
  }

}
