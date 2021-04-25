package com.alaeri.presentation.wiki

sealed class Intent{
    data class Edit(val newQuery: String): Intent()
    object NavigateToQuery: Intent()
    data class SelectNextLink(val forward: Boolean = true): Intent()
    object NavigateToSelection: Intent()
    object Exit: Intent()
    object ClearSelection : Intent()
    object ChangeSelectedTab: Intent()
}