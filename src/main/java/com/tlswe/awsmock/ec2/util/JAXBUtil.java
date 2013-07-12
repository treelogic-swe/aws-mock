package com.tlswe.awsmock.ec2.util;

import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tlswe.awsmock.common.util.PropertiesUtils;

public class JAXBUtil {

    static private Log _log = LogFactory.getLog(JAXBUtil.class);

    static JAXBContext jaxbContext = null;
    static Marshaller jaxbMarshaller = null;

    static {
        try {

            jaxbContext = JAXBContext
                    .newInstance("com.tlswe.awsmock.ec2.cxf_generated");
            jaxbMarshaller = jaxbContext.createMarshaller();

            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            jaxbMarshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");

        } catch (JAXBException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     * @param args
     * @throws JAXBException
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    public static void main(String[] args) throws JAXBException,
            NoSuchAlgorithmException, UnsupportedEncodingException {

        // DescribeInstancesResponseType d = new
        // DescribeInstancesResponseType();
        // d.setRequestId(UUID.randomUUID().toString());
        // ReservationSetType resSet = new ReservationSetType();
        // ReservationInfoType resInfo1 = new ReservationInfoType();
        // resInfo1.setOwnerId("aabb");
        // resSet.getItem().add(resInfo1);
        //
        // RunningInstancesSetType instsSet = new RunningInstancesSetType();
        //
        // RunningInstancesItemType inst = new RunningInstancesItemType();
        // inst.setInstanceId("i-mock001");
        //
        // InstanceStateType st = new InstanceStateType();
        // st.setName("stopped-my");
        //
        // inst.setInstanceState(st);
        // instsSet.getItem().add(inst);
        //
        // resInfo1.setInstancesSet(instsSet);
        //
        // d.setReservationSet(resSet);
        //
        // marshall4(d);


    }



    // private static void marshall2(Object obj) throws JAXBException {
    //
    // JAXBContext jaxbContext = JAXBContext
    // .newInstance("com.tlswe.propellerlabs.awsmock.cxf_generated");
    // Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
    // // output pretty printed
    // jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
    // jaxbMarshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
    //
    // // jaxbMarshaller.marshal(new JAXBElement(new QName(obj.getClass()
    // // .getSimpleName()), obj.getClass(), obj), System.out);
    //
    // jaxbMarshaller.marshal(new JAXBElement(new QName("aaa"),
    // obj.getClass(), obj), System.out);
    // }
    //
    // public static <T> void marshall(Class<T> clazz, T obj, Writer writer)
    // throws JAXBException {
    //
    // jaxbMarshaller.marshal(
    // new JAXBElement<T>(new QName(PropertiesUtils
    // .getProperty("xmlns.current"), obj.getClass()
    // .getSimpleName()), clazz, obj), writer);
    //
    // }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static String marshall(Object obj, String localPartQName,
            String requestVersion) throws JAXBException {

        StringWriter writer = new StringWriter();

        jaxbMarshaller.marshal(
                new JAXBElement(new QName(PropertiesUtils
                        .getProperty("xmlns.current"), localPartQName), obj
                        .getClass(), obj), writer);

        String ret = writer.toString();

        if ("true".equalsIgnoreCase(PropertiesUtils
                .getProperty("elasticfox.compatible"))
                && null != requestVersion
                && requestVersion.equals(PropertiesUtils
                        .getProperty("ec2.api.version.elasticfox"))) {
            ret = StringUtils
                    .replaceOnce(ret, PropertiesUtils
                            .getProperty("ec2.api.version.current.impl"),
                            PropertiesUtils
                                    .getProperty("ec2.api.version.elasticfox"));
        }

        // _log.info(ret);

        return ret;

    }

    // public static void marshall3(Object obj, Writer writer)
    // throws JAXBException {
    //
    // jaxbMarshaller.marshal(new JAXBElement(new QName(obj.getClass()
    // .getSimpleName()), obj.getClass(), obj), writer);
    //
    // }

}
