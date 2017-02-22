package eu.odalic.uv.dpu.transformer.odalic.model;

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * Reporting message with extra details for developers.
 * 
 * @author Václav Brodec
 *
 */
@XmlRootElement(name = "message")
public final class Message implements Serializable {

  private static final long serialVersionUID = -1606928819115845217L;

  private String text;

  private List<URI> additionalResources;

  private String debugContent;

  public Message() {}

  /**
   * @return the text
   */
  @XmlElement
  public String getText() {
    return text;
  }

  /**
   * @param text the text to set
   */
  public void setText(final String text) {
    this.text = text;
  }

  /**
   * @return the additional resources
   */
  @XmlElement
  public List<URI> getAdditionalResources() {
    return additionalResources;
  }

  /**
   * @param additionalResources the additionalResources to set
   */
  public void setAdditionalResources(final List<URI> additionalResources) {
    this.additionalResources = new ArrayList<>(additionalResources);
  }

  /**
   * @return the debug content
   */
  @XmlElement
  public String getDebugContent() {
    return debugContent;
  }

  /**
   * @param debugContent the debugContent to set
   */
  public void setDebugContent(final String debugContent) {
    this.debugContent = debugContent;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "Message [text=" + text + ", additionalResources=" + additionalResources
        + ", debugContent=" + debugContent + "]";
  }
}
