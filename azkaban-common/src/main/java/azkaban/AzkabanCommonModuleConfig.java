package azkaban;

import azakban.utils.Props;
import org.apache.log4j.Logger;

import javax.inject.Inject;

public class AzkabanCommonModuleConfig {
    private static final Logger log = Logger.getLogger(AzkabanCommonModuleConfig.class);

    private final Props props;

    @Inject

    public AzkabanCommonModuleConfig(Props props) {
        this.props = props;
    }
}
