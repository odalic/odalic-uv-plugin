/**
 * 
 */
package eu.odalic.uv.dpu.transformer.odalic.model;

import java.io.Serializable;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Execution dummy.
 * 
 * @author Václav Brodec
 *
 */
@XmlRootElement(name = "execution")
public final class Execution implements Serializable {

  private static final long serialVersionUID = 7047435888954718620L;
  
  private boolean draft = false;
  
  public Execution() {}

  /**
   * @return the draft
   */
  @XmlElement
  @Nullable
  public boolean isDraft() {
    return draft;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "Execution [draft=" + draft + "]";
  }

  /**
   * @param draft the draft to set
   */
  public void setDraft(boolean draft) {
    this.draft = draft;
  }
}
