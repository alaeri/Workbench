package com.alaeri.command

interface GenericLogger<T>{
    fun log(value: T)
}