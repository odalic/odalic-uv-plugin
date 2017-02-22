package eu.odalic.uv.dpu.transformer.odalic.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Task execution state.
 * 
 * @author Václav Brodec
 */
@XmlType
@XmlEnum(String.class)
@XmlRootElement(name = "state")
public enum State {
  /**
   * Task is specified, but not yet submitted for execution.
   */
  @XmlEnumValue("READY") READY,
  
  /**
   * Task is submitted for execution, but not done or canceled yet.
   */
  @XmlEnumValue("RUNNING") RUNNING,
  
  /**
   * Task execution has ended with success.
   */
  @XmlEnumValue("SUCCESS") SUCCESS,
  
  /**
   * Task execution has ended with warnings.
   */
  @XmlEnumValue("WARNING") WARNING,
  
  /**
   * Task execution has ended with an error.
   */
  @XmlEnumValue("ERROR") ERROR
}
