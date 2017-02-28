/**
 *
 */
package eu.odalic.uv.dpu.transformer.odalic.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * A {@link State} reply wrapper.
 *
 * @author Václav Brodec
 *
 */
@XmlRootElement(name = "reply")
public final class StateReply {

  private int status;

  private String type;

  private State payload;

  private String stamp;

  public StateReply() {}
  
  @XmlElement
  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  @XmlElement
  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  @XmlElement
  public State getPayload() {
    return payload;
  }

  public void setPayload(State payload) {
    this.payload = payload;
  }

  @XmlElement
  public String getStamp() {
    return stamp;
  }

  public void setStamp(String stamp) {
    this.stamp = stamp;
  }

  @Override
  public String toString() {
    return "StateReply [status=" + this.status + ", type=" + this.type + ", payload=" + this.payload
        + ", stamp=" + this.stamp + "]";
  }
}
