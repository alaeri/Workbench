package com.alaeri.log.repository

data class GroupKey(
    val type: Class<*>,
    val name: String?,
)