package com.alaeri.presentation.wiki

sealed class Intent{
    data class Edit(val newQuery: String): Intent()
    object NavigateToQuery: Intent()
    object SelectNextLink: Intent()
    object NavigateToSelection: Intent()
    object Exit: Intent()
}