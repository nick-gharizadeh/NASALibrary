package com.project.nasalibrary.data.repository

import com.project.nasalibrary.data.datasource.RemoteDataSource
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject

@ActivityRetainedScoped
class SearchRepository @Inject constructor(private val remote: RemoteDataSource) {
    suspend fun search(query: String) = remote.search(query)
}