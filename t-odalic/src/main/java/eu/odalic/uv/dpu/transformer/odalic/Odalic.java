package eu.odalic.uv.dpu.transformer.odalic;

import eu.unifiedviews.dpu.DPU;
import eu.unifiedviews.dpu.DPUException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.unifiedviews.helpers.dpu.config.ConfigHistory;
import eu.unifiedviews.helpers.dpu.context.ContextUtils;
import eu.unifiedviews.helpers.dpu.exec.AbstractDpu;
import eu.unifiedviews.helpers.dpu.extension.ExtensionInitializer;
import eu.unifiedviews.helpers.dpu.extension.faulttolerance.FaultTolerance;

/**
 * Main data processing unit class.
 *
 * @author Václav Brodec
 */
@DPU.AsTransformer
public class Odalic extends AbstractDpu<OdalicConfig_V1> {

    private static final Logger log = LoggerFactory.getLogger(Odalic.class);

	public Odalic() {
		super(OdalicVaadinDialog.class, ConfigHistory.noHistory(OdalicConfig_V1.class));
	}
		
    @Override
    protected void innerExecute() throws DPUException {

        ContextUtils.sendShortInfo(ctx, "Odalic.message");
        
    }
	
}
