package com.cornetelevated.corehealth.models.common

data class User(
    var userName: String?,
    var email: String?,
    var roleId: Int,
    var id: Int,
    var userId: Int,
    var token: String?,
    var isAdmin: Boolean?,
    var userNeedsToResetPassword: Boolean
)