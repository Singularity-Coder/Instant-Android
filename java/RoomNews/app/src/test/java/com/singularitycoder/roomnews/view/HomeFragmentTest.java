package com.singularitycoder.roomnews.view;

import androidx.annotation.NonNull;

import com.singularitycoder.roomnews.helper.AppConstants;
import com.singularitycoder.roomnews.helper.retrofit.ApiEndPoints;
import com.singularitycoder.roomnews.helper.retrofit.StateMediator;
import com.singularitycoder.roomnews.helper.retrofit.UiState;
import com.singularitycoder.roomnews.model.NewsItem;
import com.singularitycoder.roomnews.repository.NewsRepository;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import io.reactivex.Single;
import retrofit2.Response;

import static org.mockito.Mockito.description;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class HomeFragmentTest {

    @NonNull
    private final String country = "in";

    @NonNull
    private final String category = "technology";

    @Mock
    private ApiEndPoints apiEndPoints;

    @Mock
    private NewsRepository newsRepository;

    @Mock
    private StateMediator<Object, UiState, String, String> stateMediator;

    @Mock
    private Response<NewsItem.NewsResponse> restaurantModel;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void givenInput_onRepositoryRestaurantListApiCall_confirmValidOutput() {

        when(apiEndPoints.getNewsList(country, category, AppConstants.API_KEY))
                .thenReturn(Single.just(restaurantModel));

        newsRepository.getNewsWithRetrofit(country, category);

        final InOrder inOrder = Mockito.inOrder(newsRepository);
        inOrder.verify(stateMediator, times(1)).getStatus();
        inOrder.verify(stateMediator, description(UiState.LOADING.toString())).getStatus().toString();

        inOrder.verify(stateMediator, times(1)).getStatus();
        inOrder.verify(stateMediator, description(UiState.SUCCESS.toString())).getStatus().toString();

        inOrder.verify(stateMediator, times(1)).getStatus();
        inOrder.verify(stateMediator, description(UiState.EMPTY.toString())).getStatus().toString();

        inOrder.verify(stateMediator, times(1)).getStatus();
        inOrder.verify(stateMediator, description(UiState.ERROR.toString())).getStatus().toString();
    }

    @Test
    public void givenWrongInput_onRepositoryRestaurantListApiCall_confirmErrorOutput() {
        final Exception exception = new Exception();

        when(apiEndPoints.getNewsList(country, category, AppConstants.API_KEY))
                .thenReturn(Single.<Response<NewsItem.NewsResponse>>error(exception));

        newsRepository.getNewsWithRetrofit(country, category);

        final InOrder inOrder = Mockito.inOrder(newsRepository);
        inOrder.verify(stateMediator, times(1)).getStatus();
        inOrder.verify(stateMediator, description(UiState.LOADING.toString())).getStatus().toString();

        inOrder.verify(stateMediator, times(1)).getStatus();
        inOrder.verify(stateMediator, description(UiState.ERROR.toString())).getStatus().toString();
        verify(stateMediator, never()).getStatus().toString();  // success
    }
}