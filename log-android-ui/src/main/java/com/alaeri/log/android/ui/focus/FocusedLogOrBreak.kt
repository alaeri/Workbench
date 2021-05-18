package com.alaeri.log.android.ui.focus

import com.alaeri.log.extra.identity.IdentityRepresentation
import com.alaeri.log.serialize.serialize.SerializedLog

sealed class FocusedLogOrBreak{
    data class Focused(val serializableLog: SerializedLog<IdentityRepresentation>): FocusedLogOrBreak()
    data class Break(val index: Int, val count: Int): FocusedLogOrBreak()
}