package com.tlswe.awsmock.common.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tlswe.awsmock.common.exception.AwsMockException;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * Utilities that writes/gets string output from FreeMarker (http://freemarker.org/) templates. All templates are
 * located under a folder named "templates" in the root folder which is relative to where this class is in the
 * classpath. In case of any errors during processing such as {@link TemplateException} or {@link FileNotFoundException}
 * (missing template), an {@link AwsMockException} wrapping the original exception will be raised.
 *
 *
 * @author xma
 *
 */
public final class TemplateUtils {

    /**
     * Log writer for this class.
     */
    private static Logger log = LoggerFactory.getLogger(TemplateUtils.class);

    /**
     * Global configuration for FreeMarker.
     */
    private static Configuration conf = new Configuration();

    /**
     * Path for the folder relative to this class under which we store and load the freemarker templates.
     */
    private static final String PATH_FOR_TEMPLATES = "/templates";

    // tell FreeMarker where to load templates - from folder "templates", in
    // classpath
    static {
        conf.setClassForTemplateLoading(TemplateUtils.class, PATH_FOR_TEMPLATES);
    }


    /**
     * Constructor is made private as this is a utility class which should be always used in static way.
     */
    private TemplateUtils() {

    }


    /**
     * Process with given template + data and then put result to writer.
     *
     * @param templateFilename
     *            filename of the .ftl file
     * @param data
     *            data to fill in the template, as pairs of key-values
     * @param writer
     *            target writer to print the result
     */
    public static void write(final String templateFilename, final Map<String, Object> data, final Writer writer) {
        Template tmpl = null;
        try {
            /*-
             * note that we don't need to cache templates by ourselves since getTemplate() does that internally already
             */
            tmpl = conf.getTemplate(templateFilename);
        } catch (FileNotFoundException e1) {
            String errMsg = "FileNotFoundException: template file '" + templateFilename + "' not found";
            log.error("{}: {}", errMsg, e1.getMessage());
            throw new AwsMockException(errMsg, e1);
        } catch (IOException e) {
            String errMsg = "IOException: failed to getTemplate (filename is " + templateFilename + ")";
            log.error("{}: {}", errMsg, e.getMessage());
            throw new AwsMockException(errMsg, e);
        }

        try {
            tmpl.process(data, writer);
        } catch (TemplateException e) {
            StringBuilder dataDescription = new StringBuilder();

            if (null != data) {
                for (Map.Entry<String, Object> entry : data.entrySet()) {
                    dataDescription.append(entry.getKey() + " - " + entry.getValue()).append('\n');
                }
            }
            String errMsg = "TemplateException: failed to process template '" + templateFilename + "', with data: "
                    + dataDescription.toString()
                    + " The probable cause could be un-matching of key-values for that template. ";
            log.error("{}: {}", errMsg, e.getMessage());
            throw new AwsMockException(errMsg, e);
        } catch (IOException e) {
            String errMsg = "IOException: failed to process template and write to writer. ";
            log.error("{}: {}", errMsg, e.getMessage());
            throw new AwsMockException(errMsg, e);
        }

    }


    /**
     * Process with given template + data and get result as a string.
     *
     * @param templateName
     *            filename of the .ftl file
     * @param data
     *            data to fill in the template, as pairs of key-values
     * @return processed result from template and data
     */
    public static String get(final String templateName, final Map<String, Object> data) {
        StringWriter writer = new StringWriter();
        write(templateName, data, writer);
        return writer.toString();
    }

}
