package com.tlswe.awsmock.common.util;

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
 * Utilities that writes/gets string output from FreeMarker templates.
 *
 * @author xma
 *
 */
public final class TemplateUtils {

    /**
* TODO .
     */
    private TemplateUtils() {

    }

    /**
     * Log writer for this class.
     */
    private static Logger log = LoggerFactory.getLogger(TemplateUtils.class);

    /**
     * Global configuration for FreeMarker.
     */
    private static Configuration conf = new Configuration();

    // tell FreeMarker where to load templates - from folder "templates", in
    // classpath
    static {
        conf.setClassForTemplateLoading(TemplateUtils.class, "/templates");
    }

    /**
     * Generate result from given template and data and print to writer.
     *
     * @param templateFilename
     *            filename of the .ftl file
     * @param data
     *            data to fill in the template, as key-values
     * @param writer
     *            target writer to print the result
     * @throws AwsMockException
     *             TODO
     */
    public static void write(final String templateFilename, final Map<String, Object> data, final Writer writer)
            throws AwsMockException {

        Template tmpl = null;
        try {
            // note that we don't need to cache templates by ourselves since
            // getTemplate() does that internally already
            tmpl = conf.getTemplate(templateFilename);
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
            String errMsg = "IOException: failed to process and write to writer. ";
            log.error("{}: {}", errMsg, e.getMessage());
            throw new AwsMockException(errMsg, e);
        }

    }

    /**
     * Generate result from given template and data and get it as a string.
     *
     * @param templateName
     *            filename of the .ftl file
     * @param data
     *            data to fill in the template, as key-values
     * @return processed result from template and data
     * @throws AwsMockException
     *             TODO
     */
    public static String get(final String templateName, final Map<String, Object> data) throws AwsMockException {
        StringWriter writer = new StringWriter();
        write(templateName, data, writer);
        return writer.toString();
    }

}
