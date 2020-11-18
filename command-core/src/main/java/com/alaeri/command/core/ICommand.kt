package com.alaeri.command.core

import com.alaeri.command.CommandNomenclature

interface ICommand<R>{
    val owner: Any
    val nomenclature: CommandNomenclature
    val name: String?
}