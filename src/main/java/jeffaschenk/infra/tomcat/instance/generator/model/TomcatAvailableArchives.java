package jeffaschenk.infra.tomcat.instance.generator.model;

import lombok.Data;

import java.util.Map;
import java.util.TreeMap;

/**
 * TomcatAvailableArchives
 * 
 * Created by jeffaschenk@gmail.com on 7/3/2017.
 */
@Data
public class TomcatAvailableArchives {

    /**
     * Apache Tomcat Mirror Head URL to be used to
     * pull an existing Archive from Distribution site when
     * local copy not available.
      */
    private String apacheMirrorHeadUrl;

    /**
     * Apache Tomcat Versions Available from the above Site.
     */
    private Map<String, TomcatArchive> archives = new TreeMap<>();

    /**
     * Default Constructor
     */
    public TomcatAvailableArchives() {
    }

    /**
     * Constructor with all available parameters
     * @param apacheMirrorHeadUrl Head URL to Pull Archives from...
     */
    public TomcatAvailableArchives(String apacheMirrorHeadUrl) {
        this.apacheMirrorHeadUrl = apacheMirrorHeadUrl;
    }

    /**
     * Obtain an Available Archive by Short Name.
     *
     * Archive must be available.
     *
     * Only have one available version available at a time, or you will not get
     * the appropriate version.
     *
     * @param shortName Name to look up.
     * @return TomcatArchive based upon Short Name which is available.
     */
    public TomcatArchive getAvailableArchiveByShortName(String shortName) {
        for(TomcatArchive archive : this.getArchives().values()) {
             if (archive.isAvailable() && archive.getShortName().equalsIgnoreCase(shortName)) {
                  return archive;
             }
        }
        /**
         * No Tomcat Version archive Found to be available.
         */
        return null;
    }

    /**
     * Obtain an Available Archive by Short Name.
     *
     * Archive must be available.
     *
     * Only have one available version available at a time, or you will not get
     * the appropriate version.
     *
     * @param versionName Name to look up, could be prefixed with a "v".
     * @return TomcatArchive based upon Short Name which is available.
     */
    public TomcatArchive getLatestAvailableArchiveByVersionName(String versionName) {
        for(TomcatArchive archive : this.getArchives().values()) {
            if (archive.isAvailable() && (archive.getShortName().startsWith(versionName.substring(1)) ||
                                          archive.getShortName().startsWith(versionName))) {
                return archive;
            }
        }
        /**
         * No Tomcat Version archive Found to be available.
         */
        return null;
    }
}
