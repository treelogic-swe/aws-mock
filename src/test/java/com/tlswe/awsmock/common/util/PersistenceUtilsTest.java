package com.tlswe.awsmock.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import com.tlswe.awsmock.common.util.PersistenceUtils.PersistenceStoreType;




@RunWith(PowerMockRunner.class)
@PrepareForTest({ File.class, PersistenceUtils.class, ObjectInputStream.class,
        FileInputStream.class, ObjectOutputStream.class, FileOutputStream.class })
public class PersistenceUtilsTest {

    @Mock
    File mockedFile;

    @Mock
    ObjectInputStream ois;

    @Mock
    ObjectOutputStream oos;

    @Mock
    FileOutputStream fos;

    @Mock
    FileInputStream fis;

    @Before
    public void doInitialize() throws Exception {

        PowerMockito.spy(PersistenceUtils.class);

        PowerMockito.whenNew(File.class).withAnyArguments().thenReturn(mockedFile);

        PowerMockito.whenNew(ObjectInputStream.class).withAnyArguments().thenReturn(ois);
        PowerMockito.whenNew(FileInputStream.class).withAnyArguments().thenReturn(fis);

        PowerMockito.whenNew(ObjectOutputStream.class).withAnyArguments().thenReturn(oos);
        PowerMockito.whenNew(FileOutputStream.class).withAnyArguments().thenReturn(fos);

        Mockito.when(mockedFile.getAbsolutePath()).thenReturn("No path was given.");
    }

    @Test
    public void Test_loadAll() {
        Assert.assertTrue(PersistenceUtils.loadAll(PersistenceStoreType.EC2) == null);
    }

    @Test
    public void Test_loadAllFileNotFoundException() throws Exception {
        PowerMockito.whenNew(ObjectInputStream.class).withAnyArguments()
                .thenThrow(new FileNotFoundException("Forced FileNotFoundException"));
        Assert.assertTrue(PersistenceUtils.loadAll(PersistenceStoreType.EC2) == null);
    }

    @Test
    public void Test_loadAllIOException() throws Exception {
        PowerMockito.whenNew(ObjectInputStream.class).withAnyArguments()
                .thenThrow(new IOException("Forced IOException"));
        Assert.assertTrue(PersistenceUtils.loadAll(PersistenceStoreType.EC2) == null);
    }

    @Test
    public void Test_loadAllClassNotFoundException() throws Exception {
        PowerMockito.whenNew(ObjectInputStream.class).withAnyArguments()
                .thenThrow(new ClassNotFoundException("Forced ClassNotFoundException"));
        Assert.assertTrue(PersistenceUtils.loadAll(PersistenceStoreType.EC2) == null);
    }

    @Test
    public void Test_saveAll() {

        Mockito.when(mockedFile.getParentFile()).thenReturn(mockedFile);
        Mockito.when(mockedFile.exists()).thenReturn(false);

        PersistenceUtils.saveAll(null, PersistenceStoreType.EC2);
    }

    @Test
    public void Test_saveAllFileNotFoundException() throws Exception {

        Mockito.when(mockedFile.getParentFile()).thenReturn(mockedFile);
        Mockito.when(mockedFile.exists()).thenReturn(false);
        PowerMockito.whenNew(ObjectOutputStream.class).withAnyArguments()
                .thenThrow(new FileNotFoundException("Forced FileNotFoundException"));
        PersistenceUtils.saveAll(null, PersistenceStoreType.EC2);
    }

    @Test
    public void Test_saveAllIOException() throws Exception {

        Mockito.when(mockedFile.getParentFile()).thenReturn(mockedFile);
        Mockito.when(mockedFile.exists()).thenReturn(false);
        PowerMockito.whenNew(ObjectOutputStream.class).withAnyArguments()
                .thenThrow(new IOException("Forced IOException"));
        PersistenceUtils.saveAll(null, PersistenceStoreType.EC2);
    }

    @Test
    public void Test_loadAllForVpc() {
        Assert.assertTrue(PersistenceUtils.loadAll(PersistenceStoreType.VPC) == null);
    }

    @Test
    public void Test_loadAllForVpcFileNotFoundException() throws Exception {
        PowerMockito.whenNew(ObjectInputStream.class).withAnyArguments()
                .thenThrow(new FileNotFoundException("Forced FileNotFoundException"));
        Assert.assertTrue(PersistenceUtils.loadAll(PersistenceStoreType.VPC) == null);
    }

    @Test
    public void Test_loadAllForVpcIOException() throws Exception {
        PowerMockito.whenNew(ObjectInputStream.class).withAnyArguments()
                .thenThrow(new IOException("Forced IOException"));
        Assert.assertTrue(PersistenceUtils.loadAll(PersistenceStoreType.VPC) == null);
    }

    @Test
    public void Test_loadAllForVpcClassNotFoundException() throws Exception {
        PowerMockito.whenNew(ObjectInputStream.class).withAnyArguments()
                .thenThrow(new ClassNotFoundException("Forced ClassNotFoundException"));
        Assert.assertTrue(PersistenceUtils.loadAll(PersistenceStoreType.VPC) == null);
    }

    @Test
    public void Test_saveAllForVolume() {

        Mockito.when(mockedFile.getParentFile()).thenReturn(mockedFile);
        Mockito.when(mockedFile.exists()).thenReturn(false);

        PersistenceUtils.saveAll(null, PersistenceStoreType.VOLUME);
    }

    @Test
    public void Test_saveAllFileForVolumeNotFoundException() throws Exception {

        Mockito.when(mockedFile.getParentFile()).thenReturn(mockedFile);
        Mockito.when(mockedFile.exists()).thenReturn(false);
        PowerMockito.whenNew(ObjectOutputStream.class).withAnyArguments()
                .thenThrow(new FileNotFoundException("Forced FileNotFoundException"));
        PersistenceUtils.saveAll(null, PersistenceStoreType.VOLUME);
    }

    @Test
    public void Test_saveAllForVolumeIOException() throws Exception {

        Mockito.when(mockedFile.getParentFile()).thenReturn(mockedFile);
        Mockito.when(mockedFile.exists()).thenReturn(false);
        PowerMockito.whenNew(ObjectOutputStream.class).withAnyArguments()
                .thenThrow(new IOException("Forced IOException"));
        PersistenceUtils.saveAll(null, PersistenceStoreType.VOLUME);
    }

    @Test
    public void Test_loadAllForVolume() {
        Assert.assertTrue(PersistenceUtils.loadAll(PersistenceStoreType.VOLUME) == null);
    }

    @Test
    public void Test_loadAllForVolumeFileNotFoundException() throws Exception {
        PowerMockito.whenNew(ObjectInputStream.class).withAnyArguments()
                .thenThrow(new FileNotFoundException("Forced FileNotFoundException"));
        Assert.assertTrue(PersistenceUtils.loadAll(PersistenceStoreType.VOLUME) == null);
    }

    @Test
    public void Test_loadAllForVolumeIOException() throws Exception {
        PowerMockito.whenNew(ObjectInputStream.class).withAnyArguments()
                .thenThrow(new IOException("Forced IOException"));
        Assert.assertTrue(PersistenceUtils.loadAll(PersistenceStoreType.VOLUME) == null);
    }

    @Test
    public void Test_loadAllForVolumeClassNotFoundException() throws Exception {
        PowerMockito.whenNew(ObjectInputStream.class).withAnyArguments()
                .thenThrow(new ClassNotFoundException("Forced ClassNotFoundException"));
        Assert.assertTrue(PersistenceUtils.loadAll(PersistenceStoreType.VOLUME) == null);
    }

    @Test
    public void Test_saveAllForVPC() {

        Mockito.when(mockedFile.getParentFile()).thenReturn(mockedFile);
        Mockito.when(mockedFile.exists()).thenReturn(false);

        PersistenceUtils.saveAll(null, PersistenceStoreType.VPC);
    }

    @Test
    public void Test_saveAllFileForVpcNotFoundException() throws Exception {

        Mockito.when(mockedFile.getParentFile()).thenReturn(mockedFile);
        Mockito.when(mockedFile.exists()).thenReturn(false);
        PowerMockito.whenNew(ObjectOutputStream.class).withAnyArguments()
                .thenThrow(new FileNotFoundException("Forced FileNotFoundException"));
        PersistenceUtils.saveAll(null, PersistenceStoreType.VPC);
    }

    @Test
    public void Test_saveAllForVpcIOException() throws Exception {

        Mockito.when(mockedFile.getParentFile()).thenReturn(mockedFile);
        Mockito.when(mockedFile.exists()).thenReturn(false);
        PowerMockito.whenNew(ObjectOutputStream.class).withAnyArguments()
                .thenThrow(new IOException("Forced IOException"));
        PersistenceUtils.saveAll(null, PersistenceStoreType.VPC);
    }

    @Test
    public void Test_saveAllForVpcGetByNameIOException() throws Exception {

        Mockito.when(mockedFile.getParentFile()).thenReturn(mockedFile);
        Mockito.when(mockedFile.exists()).thenReturn(false);
        PowerMockito.whenNew(ObjectOutputStream.class).withAnyArguments()
                .thenThrow(new IOException("Forced IOException"));
        PersistenceUtils.saveAll(null, PersistenceStoreType.getByName("VPC"));
    }

    @Test
    public void Test_saveAllForVolumeGetByNameIOException() throws Exception {

        Mockito.when(mockedFile.getParentFile()).thenReturn(mockedFile);
        Mockito.when(mockedFile.exists()).thenReturn(false);
        PowerMockito.whenNew(ObjectOutputStream.class).withAnyArguments()
                .thenThrow(new IOException("Forced IOException"));
        PersistenceUtils.saveAll(null, PersistenceStoreType.getByName("VOLUME"));
    }
    
    @Test
    public void Test_getPersistenceStoreType() throws Exception {
    	
    	Assert.assertTrue("VPC Type exists", PersistenceStoreType.getByName("VPC") == PersistenceStoreType.VPC);
    }
    
    @Test
    public void Test_getPersistenceStore() throws Exception {
    	PersistenceStoreType persistenceStoreType = PersistenceStoreType.VPC;
    	Assert.assertTrue( persistenceStoreType.getStore() !=null);
    }
    
    @Test
    public void Test_containPersistenceStoreType() throws Exception {
    	
    	Assert.assertTrue("VPC Type exists", PersistenceStoreType.containsByName("VPC") == true);
    }
}
