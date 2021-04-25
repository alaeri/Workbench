package com.alaeri.presentation.wiki

import com.alaeri.log

class EditUseCase(private val queryRepository: QueryRepository){

    suspend fun edit(intent: Intent.Edit) = log(name = "edit query") {
        queryRepository.updateQuery(intent.newQuery)

    }
}