package com.singularitycoder.retrofitpostwithgson;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

// LOCAL UNIT TESTS

// new way
@RunWith(MockitoJUnitRunner.class)
public class MyRepositoryTest {

    // For fake or mock data
    @Mock
    MyRepository myRepositoryMock;

    // For real data
    @Spy
    MyRepository myRepositorySpy;

    // Set rule to make things work synchronously one at a time
    @Rule
    public InstantTaskExecutorRule mInstantTaskExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void setUp() throws Exception {
        // Old way
//        MockitoAnnotations.initMocks(Repository.class);
    }

    @Test
    public void getMockData() {
        // import static org.mockito.Mockito.when;   // ...or...
        //import static org.mockito.Mockito.*;
        // This will give u mock object
        Mockito.when(myRepositoryMock.getData()).thenReturn("this is mock string ");
        // Ths will give u real object. Use Spy to get real object
        System.out.println(myRepositoryMock.getData());
        // gives null
        Mockito.doReturn(myRepositoryMock.getData()).when(myRepositoryMock).getData();
    }

    @Test
    public void getRealData() {
        // Get Real object
        Mockito.doReturn(myRepositorySpy.getData()).when(myRepositorySpy).getData();
        // Ths will give u real object. Use Spy to get real object
        System.out.println(myRepositorySpy.getData());
    }

    @Test
    public void getMockValues() {
        Mockito.doNothing().when(myRepositoryMock).getValues("a", "b");
    }

    @Test
    public void getRealValues() {
        Mockito.doNothing().when(myRepositorySpy).getValues("a", "b");
    }

    @Test
    public void getMockMutableLiveData() {
        MutableLiveData<String> mutableLiveData = new MutableLiveData<>();
        mutableLiveData.setValue("this is login test");
        // Then return mock data
        Mockito.when(myRepositoryMock.getMutableLiveData("Hithesh", "hitwonder")).thenReturn(mutableLiveData);
        System.out.println(mutableLiveData.getValue());
    }

    @Test
    public void getRealMutableLiveData() {
        // Then return mock data
        Mockito.when(myRepositorySpy.getMutableLiveData("Hithesh", "hitwonder")).thenReturn(myRepositorySpy.mutableLiveData);
        System.out.println(myRepositorySpy.mutableLiveData.getValue());
    }
}