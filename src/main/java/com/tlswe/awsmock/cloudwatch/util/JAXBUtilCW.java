package com.tlswe.awsmock.cloudwatch.util;

import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tlswe.awsmock.cloudwatch.cxf_generated.DescribeAlarmsResponse;
import com.tlswe.awsmock.cloudwatch.cxf_generated.GetMetricStatisticsResponse;
import com.tlswe.awsmock.common.exception.AwsMockException;
import com.tlswe.awsmock.common.util.Constants;
import com.tlswe.awsmock.common.util.PropertiesUtils;

/**
 * Utility class to build XML string as AWS response from java object, using the JAXB API, working with the stub classes
 * under com.tlswe.awsmock.cloudwatch.cxf_generated.
 *
 * @author xma
 *
 */
public final class JAXBUtilCW {

    /**
     * Log writer for this class.
     */
    private static Logger log = LoggerFactory.getLogger(JAXBUtilCW.class);

    /**
     * The JAXB context working in the context path of package com.tlswe.awsmock.cloudwatch.cxf_generated.
     */
    private static JAXBContext jaxbContext = null;

    /**
     * Marshaller for building xml.
     */
    private static Marshaller jaxbMarshaller = null;

    /**
     * Package name for the generated CXF Java stub on which JAXBContext works.
     */
    private static final String CXF_STUB_PACKAGE_NAME = "com.tlswe.awsmock.cloudwatch.cxf_generated";

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
    private JAXBUtilCW() {

    }

    /**
     * marshall object to string.
     * @param obj
     *            GetMetricStatisticsResponse to be serialized and built into xml
     * @param localPartQName
     *            local part of the QName
     * @param requestVersion
     *            the version of CloudWatch API used by client (aws-sdk, cmd-line tools or other
     *            third-party client tools)
     * @return xml representation bound to the given object
     */
    public static String marshall(final GetMetricStatisticsResponse obj,
            final String localPartQName, final String requestVersion) {

        StringWriter writer = new StringWriter();

        try {
            /*-
             *  call jaxbMarshaller.marshal() synchronized (fixes the issue of
             *  java.lang.ArrayIndexOutOfBoundsException: -1
             *  at com.sun.xml.internal.bind.v2.util.CollisionCheckStack.pushNocheck(CollisionCheckStack.java:117))
             *  in case of jaxbMarshaller.marshal() is called concurrently
             */
            synchronized (jaxbMarshaller) {

                JAXBElement<GetMetricStatisticsResponse> jAXBElement = new JAXBElement<GetMetricStatisticsResponse>(
                        new QName(PropertiesUtils
                                .getProperty(Constants.PROP_NAME_CLOUDWATCH_XMLNS_CURRENT),
                                "local"),
                        GetMetricStatisticsResponse.class, obj);

                jaxbMarshaller.marshal(jAXBElement, writer);
            }
        } catch (JAXBException e) {
            String errMsg = "failed to marshall object to xml, localPartQName="
                    + localPartQName + ", requestVersion="
                    + requestVersion;
            log.error("{}, exception message: {}", errMsg, e.getLinkedException());
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
                            .getProperty(Constants.PROP_NAME_CLOUDWATCH_API_VERSION_CURRENT_IMPL),
                            PropertiesUtils
                                    .getProperty(Constants.PROP_NAME_EC2_API_VERSION_ELASTICFOX));
        }

        return ret;

    }

    /**
    *
    * @param obj
    *            DescribeAlarmsResponse to be serialized and built into xml
    * @param localPartQName
    *            local part of the QName
    * @param requestVersion
    *            the version of CloudWatch API used by client (aws-sdk, cmd-line tools or other
    *            third-party client tools)
    * @return xml representation bound to the given object
    */
   public static String marshall(final DescribeAlarmsResponse obj,
           final String localPartQName, final String requestVersion) {

       StringWriter writer = new StringWriter();

       try {
           /*-
            *  call jaxbMarshaller.marshal() synchronized (fixes the issue of
            *  java.lang.ArrayIndexOutOfBoundsException: -1
            *  at com.sun.xml.internal.bind.v2.util.CollisionCheckStack.pushNocheck(CollisionCheckStack.java:117))
            *  in case of jaxbMarshaller.marshal() is called concurrently
            */
           synchronized (jaxbMarshaller) {

               JAXBElement<DescribeAlarmsResponse> jAXBElement = new JAXBElement<DescribeAlarmsResponse>(
                       new QName(PropertiesUtils
                               .getProperty(Constants.PROP_NAME_CLOUDWATCH_XMLNS_CURRENT),
                               "local"),
                       DescribeAlarmsResponse.class, obj);

               jaxbMarshaller.marshal(jAXBElement, writer);
           }
       } catch (JAXBException e) {
           String errMsg = "failed to marshall object to xml, localPartQName="
                   + localPartQName + ", requestVersion="
                   + requestVersion;
           log.error("{}, exception message: {}", errMsg, e.getLinkedException());
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
                           .getProperty(Constants.PROP_NAME_CLOUDWATCH_API_VERSION_CURRENT_IMPL),
                           PropertiesUtils
                                   .getProperty(Constants.PROP_NAME_EC2_API_VERSION_ELASTICFOX));
       }

       return ret;

   }

}
