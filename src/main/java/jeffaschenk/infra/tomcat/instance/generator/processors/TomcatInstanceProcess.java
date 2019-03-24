package jeffaschenk.infra.tomcat.instance.generator.processors;

import jeffaschenk.infra.tomcat.instance.generator.model.TomcatInstance;

/**
 * TomcatInstanceProcess
 * 
 * Created by schenkje on 2/20/2017.
 */
public interface TomcatInstanceProcess {

    boolean performProcess(TomcatInstance tomcatInstance);
}
