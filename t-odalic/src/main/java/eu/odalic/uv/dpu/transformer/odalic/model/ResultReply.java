/**
 *
 */
package eu.odalic.uv.dpu.transformer.odalic.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * A {@link Result} reply wrapper.
 *
 * @author Václav Brodec
 *
 */
@XmlRootElement(name = "reply")
public final class ResultReply {

  private int status;

  private String type;

  private Result payload;

  private String stamp;

  public ResultReply() {}

  @XmlElement
  public Result getPayload() {
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

  public void setPayload(final Result payload) {
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
    return "ResultReply [status=" + this.status + ", type=" + this.type + ", payload="
        + this.payload + ", stamp=" + this.stamp + "]";
  }
}
