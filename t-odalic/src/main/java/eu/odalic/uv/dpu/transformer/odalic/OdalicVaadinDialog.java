package eu.odalic.uv.dpu.transformer.odalic;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;

import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.helpers.dpu.vaadin.dialog.AbstractDialog;

/**
 * Vaadin configuration dialog for Odalic.
 *
 * @author Václav Brodec
 */
public class OdalicVaadinDialog extends AbstractDialog<OdalicConfig_V1> {

  private static final long serialVersionUID = -6249024409185217114L;

  private ByteArrayOutputStream configurationStream;
  
  private TextField tokenTextField;
  
  private TextField charsetTextField;

  private TextField delimiterTextField;

  private CheckBox emptyLinesIgnoredCheckBox;

  private TextField quoteCharacterTextField;

  private TextField escapeCharacterTextField;

  private TextField commentMarkerTextField;

  private TextField lineSeparatorTextField;

  public OdalicVaadinDialog() {
    super(Odalic.class);
  }

  @Override
  public void setConfiguration(OdalicConfig_V1 c) throws DPUConfigException {
    this.tokenTextField.setValue(c.getToken() == null ? "" : c.getToken());
    this.charsetTextField.setValue(c.getCharset());
    this.delimiterTextField.setValue(String.valueOf(c.getDelimiter()));
    this.emptyLinesIgnoredCheckBox.setValue(c.isEmptyLinesIgnored());
    this.quoteCharacterTextField.setValue(c.getQuoteCharacter() == null ? "" : c.getQuoteCharacter().toString());
    this.escapeCharacterTextField.setValue(c.getEscapeCharacter() == null ? "" : c.getEscapeCharacter().toString());
    this.commentMarkerTextField.setValue(c.getCommentMarker() == null ? "" : c.getCommentMarker().toString());
    this.lineSeparatorTextField.setValue(c.getLineSeparator());
  }

  @Override
  public OdalicConfig_V1 getConfiguration() throws DPUConfigException {
    final OdalicConfig_V1 c = new OdalicConfig_V1();

    if (this.tokenTextField.getValue().isEmpty()) {
      throw new DPUConfigException(ctx.tr("Odalic.error.token.empty"));
    }
    c.setToken(this.tokenTextField.getValue());
    
    if (configurationStream == null) {
      throw new DPUConfigException(ctx.tr("Odalic.error.taskConfiguration.empty"));
    }
    c.setTaskConfiguration(new String(configurationStream.toByteArray(), StandardCharsets.UTF_8));

    try {
      Charset.forName(this.charsetTextField.getValue());
    } catch (final IllegalArgumentException e) {
      throw new DPUConfigException(ctx.tr("Odalic.error.charset.unknown"), e);
    }
    c.setCharset(this.charsetTextField.getValue());
    
    try {
      c.setDelimiter(this.delimiterTextField.getValue().charAt(0));
    } catch (final IndexOutOfBoundsException e) {
      throw new DPUConfigException(ctx.tr("Odalic.error.delimiter.empty"), e); 
    }
    
    
    c.setEmptyLinesIgnored(this.emptyLinesIgnoredCheckBox.getValue());
    
    c.setQuoteCharacter(this.quoteCharacterTextField.getValue().isEmpty() ? null : this.quoteCharacterTextField.getValue().charAt(0));
    c.setEscapeCharacter(this.escapeCharacterTextField.getValue().isEmpty() ? null : this.escapeCharacterTextField.getValue().charAt(0));
    c.setCommentMarker(this.commentMarkerTextField.getValue().isEmpty() ? null : this.commentMarkerTextField.getValue().charAt(0));
    
    if (this.lineSeparatorTextField.getValue().isEmpty()) {
      throw new DPUConfigException(ctx.tr("Odalic.error.lineSeparator.empty"));
    }
    c.setLineSeparator(this.lineSeparatorTextField.getValue());
    
    return c;
  }

  @Override
  public void buildDialogLayout() {
    final VerticalLayout mainLayout = new VerticalLayout();
    mainLayout.setWidth("100%");
    mainLayout.setHeight("-1px");
    mainLayout.setMargin(true);

    this.tokenTextField = addTextField(mainLayout, ctx.tr("Odalic.dialog.token"));
    
    mainLayout.addComponent(new Label(ctx.tr("Odalic.dialog.label")));

    final Upload upload =
        new Upload("Odalic task configuration file import", new Upload.Receiver() {

          private static final long serialVersionUID = 2495056208041024052L;

          @Override
          public OutputStream receiveUpload(String filename, String mimeType) {
            configurationStream = new ByteArrayOutputStream();
            return configurationStream;
          }
        });

    mainLayout.addComponent(upload);
    
    this.charsetTextField = addTextField(mainLayout, ctx.tr("Odalic.dialog.file.charset"));
    this.delimiterTextField = addTextField(mainLayout, ctx.tr("Odalic.dialog.file.delimiter"));
    this.emptyLinesIgnoredCheckBox = addCheckBox(mainLayout, ctx.tr("Odalic.dialog.file.emptyLinesIgnored"));
    this.quoteCharacterTextField = addTextField(mainLayout, ctx.tr("Odalic.dialog.file.quoteCharacter"), 1);
    this.escapeCharacterTextField = addTextField(mainLayout, ctx.tr("Odalic.dialog.file.escapeCharacter"), 1);
    this.commentMarkerTextField = addTextField(mainLayout, ctx.tr("Odalic.dialog.file.commentMarker"), 1);
    this.lineSeparatorTextField = addTextField(mainLayout, ctx.tr("Odalic.dialog.file.lineSeparator"));

    setCompositionRoot(mainLayout);
  }

  private static TextField addTextField(final VerticalLayout layout, final String label, final int maxLength) {
    final HorizontalLayout rowLayout = new HorizontalLayout();
    rowLayout.addComponent(new Label(label));
    
    final TextField textField = new TextField();
    textField.setMaxLength(maxLength);
    rowLayout.addComponent(textField);
    
    layout.addComponent(rowLayout);
    
    
    return textField;
  }
  
  private static TextField addTextField(final VerticalLayout layout, final String label) {
    return addTextField(layout, label, -1);
  }
  
  private static CheckBox addCheckBox(final VerticalLayout layout, final String label) {
    final HorizontalLayout rowLayout = new HorizontalLayout();
    rowLayout.addComponent(new Label(label));
    
    final CheckBox checkBox = new CheckBox();
    rowLayout.addComponent(checkBox);
    
    layout.addComponent(rowLayout);
    
    return checkBox;
  }
}
