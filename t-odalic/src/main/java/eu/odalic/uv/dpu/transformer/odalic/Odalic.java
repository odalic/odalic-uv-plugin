package eu.odalic.uv.dpu.transformer.odalic;

import eu.odalic.uv.dpu.transformer.odalic.model.Execution;
import eu.odalic.uv.dpu.transformer.odalic.model.Format;
import eu.odalic.uv.dpu.transformer.odalic.model.Message;
import eu.odalic.uv.dpu.transformer.odalic.model.MessageReply;
import eu.odalic.uv.dpu.transformer.odalic.model.ResultReply;
import eu.odalic.uv.dpu.transformer.odalic.model.State;
import eu.odalic.uv.dpu.transformer.odalic.model.StateReply;
import eu.odalic.uv.dpu.transformer.odalic.util.Io;
import eu.unifiedviews.dataunit.DataUnit;
import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.files.FilesDataUnit;
import eu.unifiedviews.dataunit.files.FilesDataUnit.Entry;
import eu.unifiedviews.dataunit.files.WritableFilesDataUnit;
import eu.unifiedviews.dpu.DPU;
import eu.unifiedviews.dpu.DPUException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status.Family;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;
import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.Value;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.Rio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.unifiedviews.helpers.dataunit.files.FilesDataUnitUtils;
import eu.unifiedviews.helpers.dataunit.files.FilesHelper;
import eu.unifiedviews.helpers.dpu.config.ConfigHistory;
import eu.unifiedviews.helpers.dpu.context.ContextUtils;
import eu.unifiedviews.helpers.dpu.exec.AbstractDpu;

/**
 * Main data processing unit class.
 *
 * @author Václav Brodec
 */
@DPU.AsTransformer
public class Odalic extends AbstractDpu<OdalicConfig_V1> {

  private static final MediaType CSV_MEDIA_TYPE = new MediaType("text", "csv");
  private static final MediaType TURTLE_MEDIA_TYPE = new MediaType("text", "turtle");

  private static final String AUTHORIZATION_HEADER_KEY = "Authorization";
  private static final String AUTHORIZATION_HEADER_CONTENT_FORMAT = "Bearer %s";

  private static final String WORKING_INPUT_FILE_FORMAT = "uv-odalic-input-%s.csv";

  private static final String TASK_CONFIGURATION_BASE_URI = "http://odalic.eu/internal/";
  private static final String TASK_ID_FORMAT = "uv-odalic-task-%s";

  private static final int STATE_QUERY_INTERVAL_MILIS = 1500;

  private static final String WORKING_OUTPUT_FILES_FORMAT = "uv-odalic-output-%s-%s.%s";

  private static final String TURTLE_WORKING_OUTPUT_FILE_PREFIX = "turtle";
  private static final String TURTLE_WORKING_OUTPUT_FILE_SUFFIX = ".ttl";

  private static final String EXTENDED_CSV_WORKING_OUTPUT_FILE_PREFIX = "extended-csv";
  private static final String EXTENDED_CSV_WORKING_OUTPUT_FILE_SUFFIX = ".csv";

  private static final String WORKING_ANNOTATED_TABLE_OUTPUT_FILE_PREFIX = "annotated-table";
  private static final String WORKING_ANNOTATED_TABLE_OUTPUT_FILE_SUFFIX = ".json";

  private static final Logger LOG = LoggerFactory.getLogger(Odalic.class);

  @DataUnit.AsInput(name = "input")
  public FilesDataUnit input;

  @DataUnit.AsOutput(name = "extendedCsvOutput")
  public WritableFilesDataUnit extendedCsvOutput;

  @DataUnit.AsOutput(name = "annotatedTableOutput")
  public WritableFilesDataUnit annotatedTableOutput;

  @DataUnit.AsOutput(name = "turtleOutput")
  public WritableFilesDataUnit turtleOutput;

  public Odalic() {
    super(OdalicVaadinDialog.class, ConfigHistory.noHistory(OdalicConfig_V1.class));
  }

  @Override
  protected void innerExecute() throws DPUException {
    ContextUtils.sendShortInfo(ctx, "Odalic.stage.start");
    ContextUtils.sendShortInfo(ctx, "Odalic.stage.input.start");

    final Client client = ClientBuilder.newBuilder().register(MultiPartFeature.class)
        .register(JacksonFeature.class).build();

    final String inputFileIdentifier = generateInputFileIdentifier();
    final File inputFile = getInputFile();
    final Format inputFileFormat = getInputFileFormat();
    LOG.info("Sending input file...");
    sendInputFile(inputFileIdentifier, inputFile, client);
    LOG.info("Input file sent.");
    LOG.info("Sending input file format...");
    sendInputFileFormat(inputFileIdentifier, inputFileFormat, client);
    LOG.info("Input file format sent.");

    ContextUtils.sendShortInfo(ctx, "Odalic.stage.input.finish");
    ContextUtils.sendShortInfo(ctx, "Odalic.stage.task.start");

    final String taskIdentifier = generateTaskIdentifier();
    final String taskConfiguration = getTaskConfiguration(inputFileIdentifier);
    LOG.info("Sending task configuration...");
    sendTaskConfigruation(taskIdentifier, taskConfiguration, client);
    LOG.info("Task configuration sent.");

    ContextUtils.sendShortInfo(ctx, "Odalic.stage.task.finish");
    ContextUtils.sendShortInfo(ctx, "Odalic.stage.execution.start");

    LOG.info("Starting exeuction...");
    startExecution(taskIdentifier, client);
    LOG.info("Execution started.");
    final State state = poll(taskIdentifier, client);
    LOG.info("Terminal state reached.");
    evaluateTerminalState(state, taskIdentifier, client);

    ContextUtils.sendShortInfo(ctx, "Odalic.stage.execution.finish");
    ContextUtils.sendShortInfo(ctx, "Odalic.stage.export.start");

    createAnnotatedTableOutput(client, taskIdentifier);
    createExtendedCsvOutput(client, taskIdentifier);
    createTurtleOutput(client, taskIdentifier);

    ContextUtils.sendShortInfo(ctx, "Odalic.stage.export.finish");
    ContextUtils.sendShortInfo(ctx, "Odalic.stage.finish");
  }

  private String getAuthorizationHeaderContent() {
    return String.format(AUTHORIZATION_HEADER_CONTENT_FORMAT, config.getToken());
  }

  private static String generateInputFileIdentifier() {
    return String.format(WORKING_INPUT_FILE_FORMAT, UUID.randomUUID());
  }

  private static String generateTaskIdentifier() {
    return String.format(TASK_ID_FORMAT, UUID.randomUUID());
  }

  private static String generateOutputFileIdentifier(final String kind, final String suffix) {
    return String.format(WORKING_OUTPUT_FILES_FORMAT, kind, UUID.randomUUID(), suffix);
  }

  private File getInputFile() throws DPUException {
    final Set<FilesDataUnit.Entry> inputEntries;
    try {
      inputEntries = FilesHelper.getFiles(this.input);
    } catch (final DataUnitException ex) {
      throw ContextUtils.dpuException(ctx, ex, "Odalic.error.input.get");
    }

    if (inputEntries.isEmpty()) {
      throw ContextUtils.dpuException(ctx, "Odalic.error.input.empty");
    }
    if (inputEntries.size() > 1) {
      throw ContextUtils.dpuException(ctx, "Odalic.error.input.multiple");
    }

    final FilesDataUnit.Entry inputEntry = inputEntries.iterator().next();

    final File inputFile;
    try {
      inputFile = FilesDataUnitUtils.asFile(inputEntry);
    } catch (final DataUnitException e1) {
      throw ContextUtils.dpuException(ctx, "Odalic.error.input.conversion");
    }

    return inputFile;
  }
  
  private Format getInputFileFormat() throws DPUException {
    final Format format = new Format();
    
    try {
      format.setCharset(config.getCharset());
    } catch (final IllegalArgumentException | NullPointerException e) {
      throw ContextUtils.dpuException(ctx, e, "Odalic.error.charset.invalid"); 
    }    
    
    if (config.getDelimiter() == null) {
      throw ContextUtils.dpuException(ctx, "Odalic.error.delimiter.empty");
    }
    format.setDelimiter(config.getDelimiter());
    
    format.setCommentMarker(config.getCommentMarker());
    format.setEmptyLinesIgnored(config.isEmptyLinesIgnored());
    format.setEscapeCharacter(config.getEscapeCharacter());
    format.setQuoteCharacter(config.getQuoteCharacter());
      
    return format;
  }

  private void sendInputFile(final String inputFileIdentifier, final File inputFile,
      final Client client) throws DPUException {
    final WebTarget target = getFilesTarget(client).path(inputFileIdentifier);

    try (final FormDataMultiPart multipartEntity = new FormDataMultiPart()) {
      final MultiPart multiPartWithFile =
          multipartEntity.bodyPart(new FileDataBodyPart("input", inputFile, CSV_MEDIA_TYPE));

      final Response response;
      try {
        response = target.request().accept(MediaType.APPLICATION_JSON_TYPE)
            .header(AUTHORIZATION_HEADER_KEY, getAuthorizationHeaderContent())
            .put(Entity.entity(multiPartWithFile, multiPartWithFile.getMediaType()));
      } catch (final ProcessingException e) {
        throw ContextUtils.dpuException(ctx, e, "Odalic.error.input.request");
      }

      if (!isSuccessful(response)) {
        throw ContextUtils.dpuException(ctx, "Odalic.error.input.upload.success");
      }
    } catch (final IOException e) {
      throw ContextUtils.dpuException(ctx, e, "Odalic.error.input.multipart");
    }
  }
  
  private void sendInputFileFormat(final String inputFileIdentifier, final Format inputFileFormat,
      final Client client) throws DPUException {
    final WebTarget target = getFilesTarget(client).path(inputFileIdentifier).path("format");

    final Response response;
    try {
      response = target.request().accept(MediaType.APPLICATION_JSON_TYPE)
          .header(AUTHORIZATION_HEADER_KEY, getAuthorizationHeaderContent())
          .put(Entity.json(inputFileFormat));
    } catch (final ProcessingException e) {
      throw ContextUtils.dpuException(ctx, e, "Odalic.error.format.request");
    }

    if (!isSuccessful(response)) {
      throw ContextUtils.dpuException(ctx, "Odalic.error.format.success");
    }
  }

  private WebTarget getFilesTarget(final Client client) {
    return client.target(config.getHost()).path("files");
  }

  private WebTarget getTasksTarget(final Client client) {
    return client.target(config.getHost()).path("tasks");
  }

  private String getTaskConfiguration(final String inputFileIdentifier) throws DPUException {
    final String serialization = config.getTaskConfiguration();
    if (serialization == null) {
      throw ContextUtils.dpuException(ctx, "Odalic.error.taskConfiguration.empty"); 
    }

    final Model model = parseTaskConfiguration(serialization);

    final Model alteredModel = injectFileIdentifier(inputFileIdentifier, model);

    return toTurtle(alteredModel);
  }

  private Model parseTaskConfiguration(final String serialization) throws DPUException {
    final Model model;
    try {
      model = Rio.parse(Io.toStream(serialization, StandardCharsets.UTF_8),
          TASK_CONFIGURATION_BASE_URI, RDFFormat.TURTLE);
    } catch (final IOException | RDFParseException e) {
      throw ContextUtils.dpuException(ctx, e, "Odalic.error.taskConfiguration.parsing");
    }
    
    return model;
  }

  private Model injectFileIdentifier(final String injected, final Model taskConfigurationModel) {
    final Model inputSubmodel = taskConfigurationModel.filter((Resource) null,
        ValueFactoryImpl.getInstance().createURI("http://odalic.eu/internal/Configuration/input"),
        (Value) null);
    final Statement inputStatement = inputSubmodel.iterator().next();

    taskConfigurationModel.remove(inputStatement);
    taskConfigurationModel.add(ValueFactoryImpl.getInstance().createStatement(
        inputStatement.getSubject(), inputStatement.getPredicate(),
        ValueFactoryImpl.getInstance().createLiteral(injected), inputStatement.getContext()));

    return taskConfigurationModel;
  }

  private String toTurtle(final Model model) throws DPUException {
    final StringWriter stringWriter = new StringWriter();
    try {
      Rio.write(model, stringWriter, RDFFormat.TURTLE);
    } catch (final RDFHandlerException e) {
      throw ContextUtils.dpuException(ctx, e, "Odalic.error.turtle.write");
    }

    return stringWriter.toString();
  }

  private void sendTaskConfigruation(final String taskIdentifier, String taskConfiguration,
      final Client client) throws DPUException {
    final WebTarget target = getTasksTarget(client).path(taskIdentifier);

    final Response response;
    try {
      response = target.request().accept(MediaType.APPLICATION_JSON_TYPE)
          .header(AUTHORIZATION_HEADER_KEY, getAuthorizationHeaderContent())
          .put(Entity.entity(taskConfiguration, TURTLE_MEDIA_TYPE));
    } catch (final ProcessingException e) {
      throw ContextUtils.dpuException(ctx, e, "Odalic.error.taskConfiguration.upload.request");
    }

    if (!isSuccessful(response)) {
      throw ContextUtils.dpuException(ctx, "Odalic.error.taskConfiguration.upload.success");
    }
  }

  private void startExecution(String taskIdentifier, Client client) throws DPUException {
    final WebTarget target = getTasksTarget(client).path(taskIdentifier).path("execution");

    final Response response;
    try {
      response = target.request().accept(MediaType.APPLICATION_JSON_TYPE)
          .header(AUTHORIZATION_HEADER_KEY, getAuthorizationHeaderContent())
          .put(Entity.json(new Execution()));
    } catch (final ProcessingException e) {
      throw ContextUtils.dpuException(ctx, e, "Odalic.error.execution.request");
    }

    if (!isSuccessful(response)) {
      throw ContextUtils.dpuException(ctx, "Odalic.error.execution.success");
    }
  }

  private State poll(final String taskIdentifier, final Client client) throws DPUException {
    State state = getState(taskIdentifier, client);
    while (state == State.RUNNING) {
      if (ctx.canceled()) {
        cancel(taskIdentifier, client);
        throw ContextUtils.dpuExceptionCancelled(ctx);
      }

      try {
        Thread.sleep(STATE_QUERY_INTERVAL_MILIS);
      } catch (final InterruptedException e) {
        throw ContextUtils.dpuException(ctx, e, "Odalic.error.interrupted");
      }

      state = getState(taskIdentifier, client);
    }
    return state;
  }

  private void cancel(final String taskIdentifier, final Client client) throws DPUException {
    final WebTarget target = getTasksTarget(client).path(taskIdentifier).path("execution");

    final Response response;
    try {
      response = target.request().accept(MediaType.APPLICATION_JSON_TYPE)
          .header(AUTHORIZATION_HEADER_KEY, getAuthorizationHeaderContent()).delete();
    } catch (final ProcessingException e) {
      throw ContextUtils.dpuException(ctx, e, "Odalic.error.execution.cancel.request");
    }

    if (!isSuccessful(response)) {
      throw ContextUtils.dpuException(ctx, "Odalic.error.execution.cancel.success");
    }
  }

  private State getState(String taskIdentifier, Client client) throws DPUException {
    final WebTarget target = getTasksTarget(client).path(taskIdentifier).path("state");

    final Response response;
    try {
      response = target.request().accept(MediaType.APPLICATION_JSON_TYPE)
          .header(AUTHORIZATION_HEADER_KEY, getAuthorizationHeaderContent()).get();
    } catch (final ProcessingException e) {
      throw ContextUtils.dpuException(ctx, e, "Odalic.error.state.request");
    }

    if (!isSuccessful(response)) {
      throw ContextUtils.dpuException(ctx, "Odalic.error.state.success");
    }

    final StateReply state;
    try {
      state = response.readEntity(StateReply.class);
    } catch (final ProcessingException e) {
      throw ContextUtils.dpuException(ctx, e, "Odalic.error.state.processing");
    }

    return state.getPayload();
  }

  private void evaluateTerminalState(final State state, final String taskIdentifier,
      final Client client) throws DPUException {
    switch (state) {
      case SUCCESS:
        ContextUtils.sendInfo(ctx, "Odalic.result.success.short", "Odalic.result.success.full");
        break;
      case WARNING:
        final String[] warnings = getWarnings(taskIdentifier, client);
        ContextUtils.sendWarn(ctx, "Odalic.result.warnings.short",
            "Odalic.error.result.warnings.full", String.join(", ", warnings));
        break;
      case ERROR:
        final Message errorMessage = getErrorMessage(taskIdentifier, client);
        throw ContextUtils.dpuException(ctx, "Odalic.error.result.error", errorMessage.getText());
      default:
        throw ContextUtils.dpuException(ctx, "Odalic.error.state.invalid");
    }
  }

  private static boolean isSuccessful(final Response response) {
    return response.getStatusInfo().getFamily() == Family.SUCCESSFUL;
  }

  private String[] getWarnings(String taskIdentifier, Client client) throws DPUException {
    final WebTarget target = getTasksTarget(client).path(taskIdentifier).path("result");

    final Response response;
    try {
      response = target.request().accept(MediaType.APPLICATION_JSON_TYPE)
          .header(AUTHORIZATION_HEADER_KEY, getAuthorizationHeaderContent()).get();
    } catch (final ProcessingException e) {
      throw ContextUtils.dpuException(ctx, e, "Odalic.error.result.request");
    }

    final ResultReply result;
    try {
      result = response.readEntity(ResultReply.class);
    } catch (final ProcessingException e) {
      throw ContextUtils.dpuException(ctx, e, "Odalic.error.result.processing");
    }

    final List<String> warningsList = result.getPayload().getWarnings();
    return warningsList.toArray(new String[warningsList.size()]);
  }

  private Message getErrorMessage(String taskIdentifier, Client client) throws DPUException {
    final WebTarget target = getTasksTarget(client).path(taskIdentifier).path("result");

    final Response response;
    try {
      response = target.request().accept(MediaType.APPLICATION_JSON_TYPE)
          .header(AUTHORIZATION_HEADER_KEY, getAuthorizationHeaderContent()).get();
    } catch (final ProcessingException e) {
      throw ContextUtils.dpuException(ctx, e, "Odalic.error.message.request");
    }

    final MessageReply message;
    try {
      message = response.readEntity(MessageReply.class);
    } catch (final ProcessingException e) {
      throw ContextUtils.dpuException(ctx, e, "Odalic.error.message.processing");
    }

    return message.getPayload();
  }

  private FilesDataUnit.Entry createOutputFile(final WritableFilesDataUnit unit,
      final String fileName) throws DPUException {
    final FilesDataUnit.Entry outputFile;
    try {
      outputFile = FilesHelper.createFile(unit, fileName);
    } catch (final DataUnitException e) {
      throw ContextUtils.dpuException(ctx, e, "Odalic.error.output.creation", fileName);
    }
    return outputFile;
  }

  private void createAnnotatedTableOutput(final Client client, final String taskIdentifier)
      throws DPUException {
    final Entry entry = createOutputFile(this.annotatedTableOutput, generateOutputFileIdentifier(
        WORKING_ANNOTATED_TABLE_OUTPUT_FILE_PREFIX, WORKING_ANNOTATED_TABLE_OUTPUT_FILE_SUFFIX));

    final File file;
    try {
      file = FilesHelper.asFile(entry);
    } catch (final DataUnitException e) {
      throw ContextUtils.dpuException(ctx, e, "Odalic.error.output.creation.annotatedTable");
    }

    writeAnnotatedTable(taskIdentifier, client, file);
  }

  private void writeAnnotatedTable(final String taskIdentifier, final Client client,
      final File outputFile) throws DPUException {
    final WebTarget target =
        getTasksTarget(client).path(taskIdentifier).path("result/annotated-table");

    final Response response;
    try {
      response = target.request().accept(MediaType.APPLICATION_JSON_TYPE)
          .header(AUTHORIZATION_HEADER_KEY, getAuthorizationHeaderContent())
          .get();
    } catch (final ProcessingException | WebApplicationException e) {
      throw ContextUtils.dpuException(ctx, e, "Odalic.error.annotatedTable.request");
    }

    try (final InputStream entityStream = (InputStream) response.getEntity();
        final OutputStream outputStream = new FileOutputStream(outputFile)) {
      Io.fastCopy(entityStream, outputStream);
    } catch (final IOException e) {
      throw ContextUtils.dpuException(ctx, e, "Odalic.error.annotatedTable.processing");
    }
  }

  private void createExtendedCsvOutput(final Client client, final String taskIdentifier)
      throws DPUException {
    final Entry entry = createOutputFile(this.extendedCsvOutput, generateOutputFileIdentifier(
        EXTENDED_CSV_WORKING_OUTPUT_FILE_PREFIX, EXTENDED_CSV_WORKING_OUTPUT_FILE_SUFFIX));

    final File file;
    try {
      file = FilesHelper.asFile(entry);
    } catch (final DataUnitException e) {
      throw ContextUtils.dpuException(ctx, e, "Odalic.error.output.creation.extendedCsv");
    }

    writeExtendedCsv(taskIdentifier, client, file);
  }

  private void writeExtendedCsv(final String taskIdentifier, final Client client,
      final File outputFile) throws DPUException {
    final WebTarget target = getTasksTarget(client).path(taskIdentifier).path("result/csv-export");

    final Response response;
    try {
      response = target.request().accept(CSV_MEDIA_TYPE)
          .header(AUTHORIZATION_HEADER_KEY, getAuthorizationHeaderContent()).get();
    } catch (final ProcessingException | WebApplicationException e) {
      throw ContextUtils.dpuException(ctx, e, "Odalic.error.extendedCsv.request");
    }

    try (final InputStream entityStream = (InputStream) response.getEntity();
        final OutputStream outputStream = new FileOutputStream(outputFile)) {
      Io.fastCopy(entityStream, outputStream);
    } catch (final IOException e) {
      throw ContextUtils.dpuException(ctx, e, "Odalic.error.extendedCsv.processing");
    }
  }

  private void createTurtleOutput(final Client client, final String taskIdentifier)
      throws DPUException {
    final Entry entry = createOutputFile(this.annotatedTableOutput, generateOutputFileIdentifier(
        TURTLE_WORKING_OUTPUT_FILE_PREFIX, TURTLE_WORKING_OUTPUT_FILE_SUFFIX));

    final File file;
    try {
      file = FilesHelper.asFile(entry);
    } catch (final DataUnitException e) {
      throw ContextUtils.dpuException(ctx, e, "Odalic.error.output.creation.turtle");
    }

    writeTurtle(taskIdentifier, client, file);
  }

  private void writeTurtle(final String taskIdentifier, final Client client, final File outputFile)
      throws DPUException {
    final WebTarget target = getTasksTarget(client).path(taskIdentifier).path("result/rdf-export");

    final Response response;
    try {
      response = target.request().accept(TURTLE_MEDIA_TYPE)
          .header(AUTHORIZATION_HEADER_KEY, getAuthorizationHeaderContent()).get();
    } catch (final ProcessingException | WebApplicationException e) {
      throw ContextUtils.dpuException(ctx, e, "Odalic.error.turtle.request");
    }

    try (final InputStream entityStream = (InputStream) response.getEntity();
        final OutputStream outputStream = new FileOutputStream(outputFile)) {
      Io.fastCopy(entityStream, outputStream);
    } catch (final IOException e) {
      throw ContextUtils.dpuException(ctx, e, "Odalic.error.turtle.processing");
    }
  }
}
