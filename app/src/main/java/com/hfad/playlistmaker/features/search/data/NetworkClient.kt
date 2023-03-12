package com.hfad.playlistmaker.features.search.data

import com.hfad.playlistmaker.features.search.data.dto.Response

interface NetworkClient {
    fun doRequest(dto: Any): Response
}