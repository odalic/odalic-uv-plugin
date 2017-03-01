package eu.odalic.uv.dpu.transformer.odalic;

import java.nio.charset.StandardCharsets;

/**
 * Configuration class for Odalic.
 *
 * @author Václav Brodec
 */
public class OdalicConfig_V1 {

  private String host;
  private String token;

  private String taskConfiguration;

  private String charset;
  private Character delimiter;
  private boolean emptyLinesIgnored;
  private Character quoteCharacter;
  private Character escapeCharacter;
  private Character commentMarker;

  public OdalicConfig_V1() {
    this.host = null;
    this.token = null;
    this.taskConfiguration = null;
    this.charset = StandardCharsets.UTF_8.name();
    this.delimiter = ';';
    this.emptyLinesIgnored = false;
    this.quoteCharacter = null;
    this.escapeCharacter = null;
    this.commentMarker = null;
  }

  /**
   * @return the comment marker
   */
  public Character getCommentMarker() {
    return this.commentMarker;
  }

  /**
   * @return the delimiter
   */
  public Character getDelimiter() {
    return this.delimiter;
  }

  /**
   * @return the escape character
   */
  public Character getEscapeCharacter() {
    return this.escapeCharacter;
  }

  /**
   * @return the host
   */
  public String getHost() {
    return this.host;
  }

  /**
   * @return the character set
   */
  public String getCharset() {
    return this.charset;
  }

  /**
   * @return the quote character
   */
  public Character getQuoteCharacter() {
    return this.quoteCharacter;
  }

  /**
   * @return the task configuration
   */
  public String getTaskConfiguration() {
    return this.taskConfiguration;
  }

  /**
   * @return the token
   */
  public String getToken() {
    return this.token;
  }

  /**
   * @return are empty lines ignored
   */
  public boolean isEmptyLinesIgnored() {
    return this.emptyLinesIgnored;
  }

  /**
   * @param commentMarker the comment marker to set
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
   * @param emptyLinesIgnored set whether the empty lines are ignored
   */
  public void setEmptyLinesIgnored(final boolean emptyLinesIgnored) {
    this.emptyLinesIgnored = emptyLinesIgnored;
  }

  /**
   * @param escapeCharacter the escape character to set
   */
  public void setEscapeCharacter(final Character escapeCharacter) {
    this.escapeCharacter = escapeCharacter;
  }

  /**
   * @param host the host to set
   */
  public void setHost(final String host) {
    this.host = host;
  }

  /**
   * @param charset the character set to set
   */
  public void setCharset(final String charset) {
    this.charset = charset;
  }

  /**
   * @param quoteCharacter the quote character to set
   */
  public void setQuoteCharacter(final Character quoteCharacter) {
    this.quoteCharacter = quoteCharacter;
  }

  /**
   * @param taskConfiguration the task configuration to set
   */
  public void setTaskConfiguration(final String taskConfiguration) {
    this.taskConfiguration = taskConfiguration;
  }

  /**
   * @param token the token to set
   */
  public void setToken(final String token) {
    this.token = token;
  }
}
