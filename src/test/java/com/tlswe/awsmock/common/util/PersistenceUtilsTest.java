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
        Assert.assertTrue(PersistenceUtils.loadAll() == null);
    }

    @Test
    public void Test_loadAllFileNotFoundException() throws Exception {
        PowerMockito.whenNew(ObjectInputStream.class).withAnyArguments()
                .thenThrow(new FileNotFoundException("Forced FileNotFoundException"));
        Assert.assertTrue(PersistenceUtils.loadAll() == null);
    }

    @Test
    public void Test_loadAllIOException() throws Exception {
        PowerMockito.whenNew(ObjectInputStream.class).withAnyArguments()
                .thenThrow(new IOException("Forced IOException"));
        Assert.assertTrue(PersistenceUtils.loadAll() == null);
    }

    @Test
    public void Test_loadAllClassNotFoundException() throws Exception {
        PowerMockito.whenNew(ObjectInputStream.class).withAnyArguments()
                .thenThrow(new ClassNotFoundException("Forced ClassNotFoundException"));
        Assert.assertTrue(PersistenceUtils.loadAll() == null);
    }

    @Test
    public void Test_saveAll() {

        Mockito.when(mockedFile.getParentFile()).thenReturn(mockedFile);
        Mockito.when(mockedFile.exists()).thenReturn(false);

        PersistenceUtils.saveAll(null);
    }

    @Test
    public void Test_saveAllFileNotFoundException() throws Exception {

        Mockito.when(mockedFile.getParentFile()).thenReturn(mockedFile);
        Mockito.when(mockedFile.exists()).thenReturn(false);
        PowerMockito.whenNew(ObjectOutputStream.class).withAnyArguments()
                .thenThrow(new FileNotFoundException("Forced FileNotFoundException"));
        PersistenceUtils.saveAll(null);
    }

    @Test
    public void Test_saveAllIOException() throws Exception {

        Mockito.when(mockedFile.getParentFile()).thenReturn(mockedFile);
        Mockito.when(mockedFile.exists()).thenReturn(false);
        PowerMockito.whenNew(ObjectOutputStream.class).withAnyArguments()
                .thenThrow(new IOException("Forced IOException"));
        PersistenceUtils.saveAll(null);
    }

}
