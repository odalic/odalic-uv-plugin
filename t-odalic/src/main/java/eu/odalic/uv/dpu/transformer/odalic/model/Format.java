package eu.odalic.uv.dpu.transformer.odalic.model;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Input file format.
 *
 * @author Václav Brodec
 */
@XmlRootElement(name = "format")
public final class Format implements Serializable {

  private static final long serialVersionUID = -1586827772971166587L;

  private String charset;
  private char delimiter;
  private boolean emptyLinesIgnored;
  private Character quoteCharacter;
  private Character escapeCharacter;
  private Character commentMarker;

  public Format() {}

  /**
   * @return the commentMarker
   */
  @XmlElement
  public Character getCommentMarker() {
    return this.commentMarker;
  }


  /**
   * @return the delimiter
   */
  @XmlElement
  public char getDelimiter() {
    return this.delimiter;
  }


  /**
   * @return the escapeCharacter
   */
  @XmlElement
  public Character getEscapeCharacter() {
    return this.escapeCharacter;
  }


  /**
   * @return the character set
   */
  @XmlElement
  public String getCharset() {
    return this.charset;
  }


  /**
   * @return the quoteCharacter
   */
  @XmlElement
  public Character getQuoteCharacter() {
    return this.quoteCharacter;
  }


  /**
   * @return the emptyLinesIgnored
   */
  @XmlElement
  public boolean isEmptyLinesIgnored() {
    return this.emptyLinesIgnored;
  }


  /**
   * @param commentMarker the commentMarker to set
   */
  public void setCommentMarker(final Character commentMarker) {
    this.commentMarker = commentMarker;
  }


  /**
   * @param delimiter the delimiter to set
   */
  public void setDelimiter(final char delimiter) {
    this.delimiter = delimiter;
  }


  /**
   * @param emptyLinesIgnored the emptyLinesIgnored to set
   */
  public void setEmptyLinesIgnored(final boolean emptyLinesIgnored) {
    this.emptyLinesIgnored = emptyLinesIgnored;
  }


  /**
   * @param escapeCharacter the escapeCharacter to set
   */
  public void setEscapeCharacter(final Character escapeCharacter) {
    this.escapeCharacter = escapeCharacter;
  }


  /**
   * @param charset the character set to set
   */
  public void setCharset(final String charset) {
    if (charset == null) {
      throw new NullPointerException("Character set cannot be null!");
    }
    
    try {
      Charset.forName(charset);
    } catch (final IllegalCharsetNameException | UnsupportedCharsetException e) {
      throw new IllegalArgumentException("Invalid character set!", e);
    }
    
    this.charset = charset;
  }


  /**
   * @param quoteCharacter the quoteCharacter to set
   */
  public void setQuoteCharacter(final Character quoteCharacter) {
    this.quoteCharacter = quoteCharacter;
  }

  @Override
  public String toString() {
    return "Format [charset=" + this.charset + ", delimiter=" + this.delimiter
        + ", emptyLinesIgnored=" + this.emptyLinesIgnored + ", quoteCharacter="
        + this.quoteCharacter + ", escapeCharacter=" + this.escapeCharacter + ", commentMarker="
        + this.commentMarker + "]";
  }
}
