/**
 *
 */
package eu.odalic.uv.dpu.transformer.odalic.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Reply wrapper.
 *
 * @author Václav Brodec
 *
 */
@XmlRootElement(name = "reply")
public final class Reply {

  private int status;

  private String type;

  private Object payload;

  private String stamp;

  public Reply() {}
  
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
  public Object getPayload() {
    return payload;
  }

  public void setPayload(Object payload) {
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
    return "Reply [status=" + this.status + ", type=" + this.type + ", payload=" + this.payload
        + ", stamp=" + this.stamp + "]";
  }
}
