package com.tlswe.awsmock.ec2.util;

import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

import org.apache.commons.lang3.StringUtils;

import com.tlswe.awsmock.common.util.PropertiesUtils;

/**
 * Utility class to build XML string as AWS response from java object, using the
 * JAXB API, working with the stub classes under
 * com.tlswe.awsmock.ec2.cxf_generated those describe the EC2 web service.
 * 
 * @author xma
 * 
 */
public class JAXBUtil {

    /**
     * the JAXB context working in the context path of package
     * com.tlswe.awsmock.ec2.cxf_generated
     */
    static JAXBContext jaxbContext = null;

    /**
     * marshaller for building xml
     */
    static Marshaller jaxbMarshaller = null;

    static {
        try {

            jaxbContext = JAXBContext.newInstance("com.tlswe.awsmock.ec2.cxf_generated");
            jaxbMarshaller = jaxbContext.createMarshaller();

            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            jaxbMarshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");

        } catch (JAXBException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     * 
     * @param obj
     *            object to be serialized and built into xml
     * @param localPartQName
     *            local part of the QName
     * @param requestVersion
     *            the version of EC2 API used by client (aws-sdk, cmd-line tools
     *            or other third-party client tools)
     * @return xml representation bound to the given object
     * @throws JAXBException
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static String marshall(Object obj, String localPartQName, String requestVersion) throws JAXBException {

        StringWriter writer = new StringWriter();

        jaxbMarshaller.marshal(new JAXBElement(new QName(PropertiesUtils.getProperty("xmlns.current"), localPartQName),
                obj.getClass(), obj), writer);

        String ret = writer.toString();

        /*- If elasticfox.compatible set to true, we replace the version number in the xml
         * to match the version of elasticfox so that it could successfully accept the xml as reponse.
         */
        if ("true".equalsIgnoreCase(PropertiesUtils.getProperty("elasticfox.compatible")) && null != requestVersion
                && requestVersion.equals(PropertiesUtils.getProperty("ec2.api.version.elasticfox"))) {
            ret = StringUtils.replaceOnce(ret, PropertiesUtils.getProperty("ec2.api.version.current.impl"),
                    PropertiesUtils.getProperty("ec2.api.version.elasticfox"));
        }

        // _log.info(ret);

        return ret;

    }

}
