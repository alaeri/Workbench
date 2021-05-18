package com.alaeri.log.android.ui.focus

import com.alaeri.log.extra.identity.IdentityRepresentation
import com.alaeri.log.serialize.serialize.SerializedLog

sealed class FocusLogItemVM{
    sealed class Empty: FocusLogItemVM(){
        data class End(val rangeEnd: RangeEnd, val focusedCount: Int, val count: Int, val onClearRange: ()->Unit): Empty()
        data class Break(val index: Int, val count: Int, val onClearFocus: ()->Unit) : Empty()
    }
    data class Content(
        val commandStateAndScope: SerializedLog<IdentityRepresentation>,
        val onItemWithIdClicked: (key : IdentityRepresentation) -> Unit
    ): FocusLogItemVM()
}