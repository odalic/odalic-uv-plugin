/**
 *
 */
package eu.odalic.uv.dpu.transformer.odalic.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * A {@link Message} reply wrapper.
 *
 * @author Václav Brodec
 *
 */
@XmlRootElement(name = "reply")
public final class MessageReply {

  private int status;

  private String type;

  private Message payload;

  private String stamp;

  public MessageReply() {}

  @XmlElement
  public Message getPayload() {
    return this.payload;
  }

  @XmlElement
  public String getStamp() {
    return this.stamp;
  }

  @XmlElement
  public int getStatus() {
    return this.status;
  }

  @XmlElement
  public String getType() {
    return this.type;
  }

  public void setPayload(final Message payload) {
    this.payload = payload;
  }

  public void setStamp(final String stamp) {
    this.stamp = stamp;
  }

  public void setStatus(final int status) {
    this.status = status;
  }

  public void setType(final String type) {
    this.type = type;
  }

  @Override
  public String toString() {
    return "MessageReply [status=" + this.status + ", type=" + this.type + ", payload="
        + this.payload + ", stamp=" + this.stamp + "]";
  }
}
