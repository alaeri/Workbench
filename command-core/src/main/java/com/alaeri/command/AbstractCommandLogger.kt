package com.alaeri.command

interface AbstractCommandLogger<T>{
    fun log(value: T)
}