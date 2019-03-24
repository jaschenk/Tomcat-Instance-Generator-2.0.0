package jeffaschenk.infra.tomcat.instance.generator.model;

import jeffaschenk.infra.tomcat.instance.generator.knowledgebase.DefaultDefinitions;
import lombok.Data;

/**
 * TomcatArchive
 *
 * Created by jeffaschenk@gmail.com on 2/22/2017.
 */
@Data
public class TomcatArchive {

    private String downloadURL;

    private String extrasURL;

    private String name;

    private String shortName;

    private boolean available;

    private Integer size;

    /**
     * TomcatArchive
     */
    public TomcatArchive() {
    }

    /**
     * TomcatArchive
     * Defines a Tomcat Archive.
     *
     * @param downloadURL - Archive Full Download URL.
     * @param extrasURL - Archive Full URL for any Extras.
     * @param name Name of Archive, example: 'apache-tomcat-8.5.xx'
     * @param available Boolean indicating if Archive is available or not.
     */
    public TomcatArchive(String downloadURL, String extrasURL,
                         String name, boolean available) {
        this.downloadURL = downloadURL;
        this.extrasURL = extrasURL;
        this.name = name;
        this.shortName = getShortVersionFromName(name);
        this.available = available;
    }

    /**
     * Helper method to contrive short Version Name from Full Name of Archive.
     * @param name reference of full Archive.
     * @return String containing obtained Short Name or Null, if unable to resolve.
     */
    public static String getShortVersionFromName(String name) {
        if (name.lastIndexOf('-')+1 <= name.length()) {
            String sname = name.substring(name.lastIndexOf('-') + 1);
            int x = sname.indexOf(DefaultDefinitions.ZIP_ARCHIVE_SUFFIX);
            if (x > 0) {
                return sname.substring(0, x);
            } else {
                return sname;
            }
        } else {
            return null;
        }
    }

    /**
     * Helper method to contrive the name of the head Directory based upon our Archive Name.
     * @return String containing contrived value.
     */
    public String getHeadName() {
        if (this.getName().indexOf(".zip'") <= name.length()) {

            int x = this.getName().indexOf(DefaultDefinitions.ZIP_ARCHIVE_SUFFIX);
            if (x > 0) {
                return this.getName().substring(0, x);
            }
        }
        /**
         * Return Original full Name...
         */
        return this.getName();
    }

    @Override
    public String toString() {
        return "TomcatArchive{" +
                "shortName='" + shortName + '\'' +
                ", name='" + name + '\'' +
                ", downloadURL='" + downloadURL + '\'' +
                ", extrasURL='" + extrasURL + '\'' +
                ", available=" + available +
                ", size=" + size +
                '}';
    }
}
