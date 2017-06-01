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

  private final JsonNode subjectColumnsPositions;

  private final JsonNode headerAnnotations;

  private final JsonNode cellAnnotations;

  private final JsonNode columnRelationAnnotations;

  private final JsonNode statisticalAnnotations;

  private final JsonNode columnProcessingAnnotations;

  private final List<String> warnings;

  @JsonCreator
  public Result(@JsonProperty("subjectColumnsPositions") final JsonNode subjectColumnsPositions,
      @JsonProperty("headerAnnotations") final JsonNode headerAnnotations,
      @JsonProperty("cellAnnotations") final JsonNode cellAnnotations,
      @JsonProperty("columnRelationAnnotations") final JsonNode columnRelationAnnotations,
      @JsonProperty("statisticalAnnotations") final JsonNode statisticalAnnotations,
      @JsonProperty("columnProcessingAnnotations") final JsonNode columnProcessingAnnotations,
      @JsonProperty("warnings") final List<String> warnings) {
    this.subjectColumnsPositions = subjectColumnsPositions;
    this.headerAnnotations = headerAnnotations;
    this.cellAnnotations = cellAnnotations;
    this.columnRelationAnnotations = columnRelationAnnotations;
    this.statisticalAnnotations = statisticalAnnotations;
    this.columnProcessingAnnotations = columnProcessingAnnotations;
    this.warnings = new ArrayList<>(warnings);
  }

  public JsonNode getCellAnnotations() {
    return this.cellAnnotations;
  }

  public JsonNode getColumnProcessingAnnotations() {
    return this.columnProcessingAnnotations;
  }

  public JsonNode getColumnRelationAnnotations() {
    return this.columnRelationAnnotations;
  }

  public JsonNode getHeaderAnnotations() {
    return this.headerAnnotations;
  }

  public JsonNode getStatisticalAnnotations() {
    return this.statisticalAnnotations;
  }

  public JsonNode getSubjectColumnPositions() {
    return this.subjectColumnsPositions;
  }

  public List<String> getWarnings() {
    return this.warnings;
  }
}
