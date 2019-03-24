package jeffaschenk.infra.tomcat.instance.generator.util;

import jeffaschenk.infra.tomcat.instance.generator.model.TomcatArchive;
import jeffaschenk.infra.tomcat.instance.generator.model.TomcatAvailableArchives;
import jeffaschenk.infra.tomcat.instance.generator.knowledgebase.DefaultDefinitions;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Optional;

/**
 *  TomcatArchiveUtility
 *
 * @author schenkje 
 */
public class TomcatArchiveUtility {
    /**
     * Do not Allow Instantiation of this Object.
     */
    private TomcatArchiveUtility(){}

    /**
     * resolveApacheMirror
     *
     * @return TomcatAvailableArchives instantiated.
     */
    public static TomcatAvailableArchives resolveApacheMirror() {
        /**
         * Check for an Available Archive Configuration File...
         */
        String apacheMirrorHeadUrl = System.getProperty(DefaultDefinitions.TOMCAT_APACHE_MIRROR_PROPERTY_NAME);
        if (apacheMirrorHeadUrl == null || apacheMirrorHeadUrl.isEmpty()) {
            return new TomcatAvailableArchives();
        }
        /**
         * We have a specified Apache Mirror URL, so we can find out what is available...
         */
        return new TomcatAvailableArchives(apacheMirrorHeadUrl);
    }

    /**
     * getLatest Archives Available from the specified Archives Mirror Head URL.
     * @param LOGGER reference
     * @param tomcatAvailableArchives reference to Achives Object to be populated.
     */
    public static void getLatest(Logger LOGGER, TomcatAvailableArchives tomcatAvailableArchives) {
        CloseableHttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(tomcatAvailableArchives.getApacheMirrorHeadUrl());
        try {
            HttpResponse response = client.execute(request);
            ConsoleOutput.out(Color.WHITE_BOLD,"  Using URL: %s Obtained Response Code: %d",
                    false, true, true,
                    tomcatAvailableArchives.getApacheMirrorHeadUrl(),
                    response.getStatusLine().getStatusCode());
            if (response.getStatusLine().getStatusCode()!=200) {
                ConsoleOutput.out(Color.RED_BOLD, "  Error: Unable to continue, due to returned Response Code!",
                        true, true, true);
            }

            Optional<Document> docResult = getDocument(LOGGER, tomcatAvailableArchives.getApacheMirrorHeadUrl());
            if (docResult.isPresent()) {
                Document doc = docResult.get();
                doc.select("a").
                        forEach(element -> processAnchorNodes(LOGGER, tomcatAvailableArchives, element));
            }
        } catch(IOException ioe) {
           LOGGER.warn("IO Exception: {} occurred while accessing URL: {}",
                   ioe.getMessage(),
                   tomcatAvailableArchives.getApacheMirrorHeadUrl());
        }
    }

    /**
     * processAnchorNodes -- Process Anchor Nodes against HTML Content
     * @param LOGGER Reference
     * @param tomcatAvailableArchives Reference
     * @param element Anchor Node Document Content.
     */
    private static void processAnchorNodes(Logger LOGGER,
                                           TomcatAvailableArchives tomcatAvailableArchives,
                                           Element element) {
        if (element.hasAttr("href")) {
            String href = element.attr("href");
            if (href.startsWith("tomcat-") && href.endsWith("/")) {
                if (href.equalsIgnoreCase("tomcat-7/") ||
                        href.equalsIgnoreCase("tomcat-connectors/")) {
                    return; 
                }
                Optional<Document> docResult = getDocument(LOGGER, element.baseUri()+href);
                if (docResult.isPresent()) {
                    Document doc = docResult.get();
                    doc.select("a")
                            .forEach(innerElement->processInnerAnchorNodes(LOGGER,
                                    tomcatAvailableArchives, innerElement));
                }
            }
        }
    }

    /**
     * processInnerAnchorNodes
     *
     * @param LOGGER Reference
     * @param tomcatAvailableArchives Reference
     * @param element Reference of Document within a HTML Page.
     */
    private static void processInnerAnchorNodes(Logger LOGGER,
                                                TomcatAvailableArchives tomcatAvailableArchives,
                                                Element element) {
        if (element.hasAttr("href")) {
            String href = element.attr("href");
            if (href.startsWith("v") && href.endsWith("/")) {
                if (href.startsWith("v8.0.")) {
                    return;
                }
                Optional<Document> docResult = getDocument(LOGGER, element.baseUri()+href+"bin");
                if (docResult.isPresent()) {
                    Document doc = docResult.get();
                    doc.select("a")
                            .forEach(innerElement->processFileAnchorNodes(LOGGER,
                                    tomcatAvailableArchives, innerElement));
                }
            }
        }
    }

    /**
     * processFileAnchorNodes
     * @param LOGGER Reference
     * @param tomcatAvailableArchives Reference
     * @param element -- Document Element Reference
     */
    private static void processFileAnchorNodes(Logger LOGGER,
                                                TomcatAvailableArchives tomcatAvailableArchives,
                                                Element element) {
        if (element.hasAttr("href")) {
            String href = element.attr("href");
            if(href.endsWith(DefaultDefinitions.ZIP_ARCHIVE_SUFFIX) && !href.contains("deployer") && !href.contains("windows")) {
                // Save File Download Information for this Archive...
                TomcatArchive tomcatArchive = new TomcatArchive(element.baseUri()+href,
                        element.baseUri()+"extras/", href, true);
                tomcatArchive.setSize(getFileSize(element.baseUri()+href));
                /**
                 * If size not available, then indicate Archive not available for now ...
                 */
                if (tomcatArchive.getSize() == null || tomcatArchive.getSize() <= 0) {
                    tomcatArchive.setAvailable(false);
                }
                tomcatAvailableArchives.getArchives().put(tomcatArchive.getShortName(), tomcatArchive);
            }
        }
    }

    /**
     * getDocument -- helper method to obtain an HTML Document from specified URL.
     * @param LOGGER - Reference
     * @param url - Resource Reference via URL to obtain.
     * @return Optional<Document> - Containing Document Contents of Resource Get request.
     */
    private static Optional<Document> getDocument(Logger LOGGER, String url) {
        try {
           return Optional.of(Jsoup.connect(url).get());
        } catch(IOException ioe) {
            LOGGER.warn("IO Exception: {} occurred while accessing URL: {}",
                    ioe.getMessage(),
                    url);
        }
        return Optional.empty();
    }

    /**
     * Get initial File Size of a Download Image.
     * @param url Resource Reference via URL
     * @return int containing size of file.
     */
    private static int getFileSize(String url) {
        URLConnection connection;
        try {
            connection = new URL(url).openConnection();
            return connection.getContentLength();
        } catch(IOException ioe) {
            return 0;
        }
    }
}
