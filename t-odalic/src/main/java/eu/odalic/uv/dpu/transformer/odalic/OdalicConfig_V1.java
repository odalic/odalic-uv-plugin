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
  private char delimiter;
  private boolean emptyLinesIgnored;
  private Character quoteCharacter;
  private Character escapeCharacter;
  private Character commentMarker;
  private String lineSeparator;

  public OdalicConfig_V1() {
    this.host = "http://odalic.eu/demo/odalic";
    this.token = null;
    this.taskConfiguration = null;
    this.charset = StandardCharsets.UTF_8.name();
    this.delimiter = ';';
    this.emptyLinesIgnored = false;
    this.quoteCharacter = null;
    this.escapeCharacter = null;
    this.commentMarker = null;
    this.lineSeparator = System.lineSeparator();
  }

  /**
   * @return the host
   */
  public String getHost() {
    return host;
  }

  /**
   * @param host the host to set
   */
  public void setHost(String host) {
    this.host = host;
  }

  /**
   * @return the token
   */
  public String getToken() {
    return token;
  }

  /**
   * @param token the token to set
   */
  public void setToken(String token) {
    this.token = token;
  }

  /**
   * @return the task configuration
   */
  public String getTaskConfiguration() {
    return taskConfiguration;
  }

  /**
   * @param taskConfiguration the task configuration to set
   */
  public void setTaskConfiguration(String taskConfiguration) {
    this.taskConfiguration = taskConfiguration;
  }
  
  /**
   * @return the character set
   */
  public String getCharset() {
    return charset;
  }

  /**
   * @param charset the character set to set
   */
  public void setCharset(String charset) {
    this.charset = charset;
  }

  /**
   * @return the delimiter
   */
  public char getDelimiter() {
    return delimiter;
  }

  /**
   * @param delimiter the delimiter to set
   */
  public void setDelimiter(char delimiter) {
    this.delimiter = delimiter;
  }

  /**
   * @return are empty lines ignored
   */
  public boolean isEmptyLinesIgnored() {
    return emptyLinesIgnored;
  }

  /**
   * @param emptyLinesIgnored set whether the empty lines are ignored
   */
  public void setEmptyLinesIgnored(boolean emptyLinesIgnored) {
    this.emptyLinesIgnored = emptyLinesIgnored;
  }

  /**
   * @return the quote character
   */
  public Character getQuoteCharacter() {
    return quoteCharacter;
  }

  /**
   * @param quoteCharacter the quote character to set
   */
  public void setQuoteCharacter(Character quoteCharacter) {
    this.quoteCharacter = quoteCharacter;
  }

  /**
   * @return the escape character
   */
  public Character getEscapeCharacter() {
    return escapeCharacter;
  }

  /**
   * @param escapeCharacter the escape character to set
   */
  public void setEscapeCharacter(Character escapeCharacter) {
    this.escapeCharacter = escapeCharacter;
  }

  /**
   * @return the comment marker
   */
  public Character getCommentMarker() {
    return commentMarker;
  }

  /**
   * @param commentMarker the comment marker to set
   */
  public void setCommentMarker(Character commentMarker) {
    this.commentMarker = commentMarker;
  }

  /**
   * @return the line separator
   */
  public String getLineSeparator() {
    return lineSeparator;
  }

  /**
   * @param lineSeparator the line separator to set
   */
  public void setLineSeparator(String lineSeparator) {
    this.lineSeparator = lineSeparator;
  }
}
