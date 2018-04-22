package azkaban;

import azakban.AzkabanCoreModule;
import azakban.utils.Props;
import com.google.inject.AbstractModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AzkabanCommonModule extends AbstractModule {

    private static final Logger log = LoggerFactory.getLogger(AzkabanCommonModule.class);

    private final Props props;
    private final AzkabanCommonModuleConfig config;

    public AzkabanCommonModule(final Props props) {
        this.props = props;
        this.config = new AzkabanCommonModuleConfig(props);
    }

    @Override
    protected void configure() {
        install(new AzkabanCoreModule(this.props));
    }
}
