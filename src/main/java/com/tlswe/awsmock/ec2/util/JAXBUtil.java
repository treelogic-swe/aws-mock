package com.tlswe.awsmock.ec2.util;

import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tlswe.awsmock.common.exception.AwsMockException;
import com.tlswe.awsmock.common.util.Constants;
import com.tlswe.awsmock.common.util.PropertiesUtils;

/**
 * Utility class to build XML string as AWS response from java object, using the JAXB API, working with the stub classes
 * under com.tlswe.awsmock.ec2.cxf_generated those describe the EC2 web service.
 *
 * @author xma
 *
 */
public final class JAXBUtil {

    /**
     * Log writer for this class.
     */
    private static Logger log = LoggerFactory.getLogger(JAXBUtil.class);

    /**
     * The JAXB context working in the context path of package com.tlswe.awsmock.ec2.cxf_generated.
     */
    private static JAXBContext jaxbContext = null;

    /**
     * Marshaller for building xml.
     */
    private static Marshaller jaxbMarshaller = null;

    /**
     * Package name for the generated CXF Java stub on which JAXBContext works.
     */
    private static final String CXF_STUB_PACKAGE_NAME = "com.tlswe.awsmock.ec2.cxf_generated";

    static {
        try {

            jaxbContext = JAXBContext
                    .newInstance(CXF_STUB_PACKAGE_NAME);
            jaxbMarshaller = jaxbContext.createMarshaller();

            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            jaxbMarshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");

        } catch (JAXBException e) {
            log.error(
                    "JAXBException caught during initializing JAXBContext Marshaller: {}",
                    e.getMessage());
        }

    }


    /**
    *
    */
    private JAXBUtil() {

    }


    /**
     *
     * @param obj
     *            object to be serialized and built into xml
     * @param localPartQName
     *            local part of the QName
     * @param requestVersion
     *            the version of EC2 API used by client (aws-sdk, cmd-line tools or other third-party client tools)
     * @return xml representation bound to the given object
     */
    public static String marshall(final Object obj, final String localPartQName, final String requestVersion) {

        StringWriter writer = new StringWriter();

        try {
            /*-
             *  call jaxbMarshaller.marshal() synchronized (fixes the issue of
             *  java.lang.ArrayIndexOutOfBoundsException: -1
             *  at com.sun.xml.internal.bind.v2.util.CollisionCheckStack.pushNocheck(CollisionCheckStack.java:117))
             *  in case of jaxbMarshaller.marshal() is called concurrently
             */
            synchronized (jaxbMarshaller) {

                jaxbMarshaller.marshal(new JAXBElement<Object>(new QName(
                        PropertiesUtils.getProperty(Constants.PROP_NAME_XMLNS_CURRENT),
                        localPartQName), Object.class, obj), writer);
            }
        } catch (JAXBException e) {
            String errMsg = "failed to marshall object to xml, localPartQName="
                    + localPartQName + ", requestVersion="
                    + requestVersion;
            log.error("{}, exception message: {}", errMsg, e.getMessage());
            throw new AwsMockException(errMsg, e);
        }

        String ret = writer.toString();

        /*- If elasticfox.compatible set to true, we replace the version number in the xml
         * to match the version of elasticfox so that it could successfully accept the xml as reponse.
         */
        if ("true".equalsIgnoreCase(PropertiesUtils
                .getProperty(Constants.PROP_NAME_ELASTICFOX_COMPATIBLE))
                && null != requestVersion
                && requestVersion.equals(PropertiesUtils
                        .getProperty(Constants.PROP_NAME_EC2_API_VERSION_ELASTICFOX))) {
            ret = StringUtils
                    .replaceOnce(ret, PropertiesUtils
                            .getProperty(Constants.PROP_NAME_EC2_API_VERSION_CURRENT_IMPL),
                            PropertiesUtils
                                    .getProperty(Constants.PROP_NAME_EC2_API_VERSION_ELASTICFOX));
        }

        return ret;

    }

}
