package com.example.aquallera

data class User(
    val fullName: String="",
    val email: String="",
    val number: String="",
    val password: String="",
    val confirmPassword: String=""
) {
    constructor() : this("","", "","","")
}