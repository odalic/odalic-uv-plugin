package eu.odalic.uv.dpu.transformer.odalic.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Partial mapping of task processing result.
 * 
 * @author Václav Brodec
 *
 */
public final class Result implements Serializable {

  private static final long serialVersionUID = -6359038623760039155L;

  private JsonNode subjectColumnPositions;

  private JsonNode headerAnnotations;

  private JsonNode cellAnnotations;

  private JsonNode columnRelationAnnotations;

  private JsonNode statisticalAnnotations;

  private JsonNode columnProcessingAnnotations;

  private List<String> warnings;

  @JsonCreator
  public Result(@JsonProperty("subjectColumnPositions") final JsonNode subjectColumnPositions,
      @JsonProperty("headerAnnotations") final JsonNode headerAnnotations,
      @JsonProperty("cellAnnotations") final JsonNode cellAnnotations,
      @JsonProperty("columnRelationAnnotations") final JsonNode columnRelationAnnotations,
      @JsonProperty("statisticalAnnotations") final JsonNode statisticalAnnotations,
      @JsonProperty("columnProcessingAnnotations") final JsonNode columnProcessingAnnotations,
      @JsonProperty("warnings") final List<String> warnings) {
    this.subjectColumnPositions = subjectColumnPositions;
    this.headerAnnotations = headerAnnotations;
    this.cellAnnotations = cellAnnotations;
    this.columnRelationAnnotations = columnRelationAnnotations;
    this.statisticalAnnotations = statisticalAnnotations;
    this.columnProcessingAnnotations = columnProcessingAnnotations;
    this.warnings = new ArrayList<>(warnings);
  }

  public JsonNode getSubjectColumnPositions() {
    return subjectColumnPositions;
  }

  public JsonNode getHeaderAnnotations() {
    return headerAnnotations;
  }

  public JsonNode getCellAnnotations() {
    return cellAnnotations;
  }

  public JsonNode getColumnRelationAnnotations() {
    return columnRelationAnnotations;
  }

  public JsonNode getStatisticalAnnotations() {
    return statisticalAnnotations;
  }

  public JsonNode getColumnProcessingAnnotations() {
    return columnProcessingAnnotations;
  }

  public List<String> getWarnings() {
    return warnings;
  }
}
