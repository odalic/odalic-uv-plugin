package eu.odalic.uv.dpu.transformer.odalic;

import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.helpers.dpu.vaadin.dialog.AbstractDialog;

/**
 * Vaadin configuration dialog for Odalic.
 *
 * @author Václav Brodec
 */
public class OdalicVaadinDialog extends AbstractDialog<OdalicConfig_V1> {

    public OdalicVaadinDialog() {
        super(Odalic.class);
    }

    @Override
    public void setConfiguration(OdalicConfig_V1 c) throws DPUConfigException {

    }

    @Override
    public OdalicConfig_V1 getConfiguration() throws DPUConfigException {
        final OdalicConfig_V1 c = new OdalicConfig_V1();

        return c;
    }

    @Override
    public void buildDialogLayout() {
        final VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setWidth("100%");
        mainLayout.setHeight("-1px");
        mainLayout.setMargin(true);

        mainLayout.addComponent(new Label(ctx.tr("Odalic.dialog.label")));

        setCompositionRoot(mainLayout);
    }
}
