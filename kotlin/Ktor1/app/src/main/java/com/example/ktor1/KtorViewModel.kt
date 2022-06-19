package com.example.ktor1

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ktor1.model.GithubUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class KtorViewModel @Inject constructor(
    application: Application,
    val repository: KtorRepository,
) : AndroidViewModel(application) {

    private val _githubUserListSharedFlow = MutableSharedFlow<List<GithubUser>>()
    val githubUserListSharedFlow = _githubUserListSharedFlow.asSharedFlow()

    private val _githubUserListStateFlow = MutableStateFlow<List<GithubUser>>(emptyList())
    val githubUserListStateFlow = _githubUserListStateFlow.asSharedFlow()

    fun loadGithubUserListFromSharedFlow() = viewModelScope.launch {
        repository
            .getGithubUserList(getApplication())
            .catch { it: Throwable ->
                _githubUserListSharedFlow.emit(emptyList())
            }
            .collect { it: List<GithubUser> ->
                _githubUserListSharedFlow.emit(it)
            }
    }

    fun loadGithubUserListFromStateFlow() = viewModelScope.launch {
        repository
            .getGithubUserList(getApplication())
            .catch { it: Throwable ->
                _githubUserListStateFlow.value = emptyList()
            }
            .collect { it: List<GithubUser> ->
                _githubUserListStateFlow.value = it
            }
    }
}