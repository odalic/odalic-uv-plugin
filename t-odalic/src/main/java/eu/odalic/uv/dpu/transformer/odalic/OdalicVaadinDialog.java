package eu.odalic.uv.dpu.transformer.odalic;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Layout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;

import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.helpers.dpu.context.ContextUtils;
import eu.unifiedviews.helpers.dpu.vaadin.dialog.AbstractDialog;

/**
 * Vaadin configuration dialog for Odalic.
 *
 * @author Václav Brodec
 */
public class OdalicVaadinDialog extends AbstractDialog<OdalicConfig_V1> {

  private static class TaskConfigurationReceiver implements Upload.Receiver {

    private static final long serialVersionUID = 7509022889268241224L;

    private ByteArrayOutputStream configurationStream = null;

    public String get() throws IOException {
      LOG.debug("Getting from the stream...");

      this.configurationStream.flush();
      this.configurationStream.close();

      return new String(this.configurationStream.toByteArray(), StandardCharsets.UTF_8);
    }

    public boolean isReady() {
      LOG.debug("Getting state of {}.", this.configurationStream);

      return this.configurationStream != null;
    }

    @Override
    public OutputStream receiveUpload(final String filename, final String mimeType) {
      LOG.debug("Upload received.");

      this.configurationStream = new ByteArrayOutputStream();
      return this.configurationStream;
    }

    public void set(final String taskConfiguration) throws IOException {
      LOG.debug("Writing the current task configuration to stream.");
      if (taskConfiguration == null) {
        LOG.debug("No such exists.");
        return;
      }

      this.configurationStream = new ByteArrayOutputStream();
      this.configurationStream.write(taskConfiguration.getBytes(StandardCharsets.UTF_8));
      this.configurationStream.flush();
      LOG.debug("Current task configuration written.");
    }
  }

  private static final Logger LOG = LoggerFactory.getLogger(OdalicVaadinDialog.class);

  private static final long serialVersionUID = -6249024409185217114L;

  private static CheckBox addCheckBox(final Layout layout, final String label) {
    final CheckBox checkBox = new CheckBox(label);

    layout.addComponent(checkBox);

    return checkBox;
  }

  private static TextField addTextField(final Layout layout, final String label, final int emSize) {
    return addTextField(layout, label, -1, emSize);
  }

  private static TextField addTextField(final Layout layout, final String label,
      final int maxLength, final int emSize) {
    final TextField textField = new TextField(label);
    textField.setMaxLength(maxLength);
    textField.setWidth(emSize, Unit.EM);

    layout.addComponent(textField);

    return textField;
  }

  private final TaskConfigurationReceiver taskConfigurationReceiver;

  private TextField hostTextField;

  private TextField tokenTextField;

  private TextField charsetTextField;

  private TextField delimiterTextField;

  private CheckBox emptyLinesIgnoredCheckBox;

  private TextField quoteCharacterTextField;

  private TextField escapeCharacterTextField;

  private TextField commentMarkerTextField;

  public OdalicVaadinDialog() {
    super(Odalic.class);

    this.taskConfigurationReceiver = new TaskConfigurationReceiver();
  }

  @Override
  public void buildDialogLayout() {
    final VerticalLayout mainLayout = new VerticalLayout();
    mainLayout.setSpacing(true);
    mainLayout.setWidth("100%");
    mainLayout.setHeight("-1px");
    mainLayout.setMargin(true);

    final HorizontalLayout connectionLayout = new HorizontalLayout();
    connectionLayout.setCaption("Server connection");
    connectionLayout.setSpacing(true);

    this.hostTextField = addTextField(connectionLayout, this.ctx.tr("Odalic.dialog.host"), 20);
    this.tokenTextField = addTextField(connectionLayout, this.ctx.tr("Odalic.dialog.token"), 50);
    mainLayout.addComponent(connectionLayout);

    final VerticalLayout inputLayout = new VerticalLayout();
    inputLayout.setSpacing(true);
    inputLayout.setWidth("100%");
    inputLayout.setHeight("-1px");

    final Upload upload = new Upload(this.ctx.tr("Odalic.dialog.task.configuration"), this.taskConfigurationReceiver);
    inputLayout.addComponent(upload);

    this.charsetTextField =
        addTextField(inputLayout, this.ctx.tr("Odalic.dialog.file.charset"), 10);
    this.delimiterTextField =
        addTextField(inputLayout, this.ctx.tr("Odalic.dialog.file.delimiter"), 1, 3);
    this.emptyLinesIgnoredCheckBox =
        addCheckBox(inputLayout, this.ctx.tr("Odalic.dialog.file.emptyLinesIgnored"));
    this.quoteCharacterTextField =
        addTextField(inputLayout, this.ctx.tr("Odalic.dialog.file.quoteCharacter"), 1, 3);
    this.escapeCharacterTextField =
        addTextField(inputLayout, this.ctx.tr("Odalic.dialog.file.escapeCharacter"), 1, 3);
    this.commentMarkerTextField =
        addTextField(inputLayout, this.ctx.tr("Odalic.dialog.file.commentMarker"), 1, 3);

    mainLayout.addComponent(inputLayout);

    setCompositionRoot(mainLayout);
  }

  @Override
  public OdalicConfig_V1 getConfiguration() throws DPUConfigException {
    LOG.debug("Getting the configuration...");

    final OdalicConfig_V1 c = new OdalicConfig_V1();

    if (this.hostTextField.getValue().isEmpty()) {
      throw new DPUConfigException(this.ctx.tr("Odalic.error.host.empty"));
    }
    try {
      URI.create(this.hostTextField.getValue());
    } catch (final IllegalArgumentException e) {
      throw new DPUConfigException(this.ctx.tr("Odalic.error.host.format"), e);
    }
    c.setHost(this.hostTextField.getValue());

    if (this.tokenTextField.getValue().isEmpty()) {
      throw new DPUConfigException(this.ctx.tr("Odalic.error.token.empty"));
    }
    c.setToken(this.tokenTextField.getValue());

    ContextUtils.sendShortInfo(this.ctx, "Attempt to get task config.");
    if (this.taskConfigurationReceiver.isReady()) {
      LOG.debug("Task config ready.");

      try {
        c.setTaskConfiguration(this.taskConfigurationReceiver.get());
        LOG.debug("Task config set.");
      } catch (final IOException e) {
        throw new DPUConfigException(e);
      }
    } else {
      LOG.debug("Task config not ready.");
    }

    if (!this.charsetTextField.getValue().isEmpty()) {
      try {
        Charset.forName(this.charsetTextField.getValue());
      } catch (final IllegalCharsetNameException | UnsupportedCharsetException e) {
        throw new DPUConfigException(this.ctx.tr("Odalic.error.charset.invalid"), e);
      }
      c.setCharset(this.charsetTextField.getValue());
    }

    c.setDelimiter(this.delimiterTextField.getValue().isEmpty() ? null
        : this.delimiterTextField.getValue().charAt(0));

    c.setEmptyLinesIgnored(this.emptyLinesIgnoredCheckBox.getValue());

    c.setQuoteCharacter(this.quoteCharacterTextField.getValue().isEmpty() ? null
        : this.quoteCharacterTextField.getValue().charAt(0));
    c.setEscapeCharacter(this.escapeCharacterTextField.getValue().isEmpty() ? null
        : this.escapeCharacterTextField.getValue().charAt(0));
    c.setCommentMarker(this.commentMarkerTextField.getValue().isEmpty() ? null
        : this.commentMarkerTextField.getValue().charAt(0));

    return c;
  }

  @Override
  public void setConfiguration(final OdalicConfig_V1 c) throws DPUConfigException {
    LOG.debug("Setting the configuration...");

    try {
      this.taskConfigurationReceiver.set(c.getTaskConfiguration());
    } catch (final IOException e) {
      throw new DPUConfigException(e);
    }

    this.hostTextField.setValue(c.getHost() == null ? "" : c.getHost());
    this.tokenTextField.setValue(c.getToken() == null ? "" : c.getToken());
    this.charsetTextField.setValue(c.getCharset());
    this.delimiterTextField.setValue(String.valueOf(c.getDelimiter()));
    this.emptyLinesIgnoredCheckBox.setValue(c.isEmptyLinesIgnored());
    this.quoteCharacterTextField
        .setValue(c.getQuoteCharacter() == null ? "" : c.getQuoteCharacter().toString());
    this.escapeCharacterTextField
        .setValue(c.getEscapeCharacter() == null ? "" : c.getEscapeCharacter().toString());
    this.commentMarkerTextField
        .setValue(c.getCommentMarker() == null ? "" : c.getCommentMarker().toString());
  }
}
