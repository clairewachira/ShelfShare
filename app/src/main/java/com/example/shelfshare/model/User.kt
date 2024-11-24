package com.example.shelfshare.model

data class User(
    val id: String = "",
    val isAnonymous: Boolean = true
)


data class UserProfile(
    val email: String = "",
    val username: String = "",
    val profilePictureUrl: String = ""
)