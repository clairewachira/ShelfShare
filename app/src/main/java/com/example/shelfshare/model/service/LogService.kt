package com.example.shelfshare.model.service

interface LogService {
    fun logNonFatalCrash(throwable: Throwable)
}