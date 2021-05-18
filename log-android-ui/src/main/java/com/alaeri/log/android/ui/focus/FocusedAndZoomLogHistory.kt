package com.alaeri.log.android.ui.focus

import com.alaeri.log.extra.identity.IdentityRepresentation

data class FocusedAndZoomLogHistory(
    val minTime: Long,
    val beforeCount: Int,
    val beforeFocusedCount: Int,
    val start: Long,
    val focus: IdentityRepresentation?,
    val end: Long,
    val afterCount: Int,
    val afterFocusedCount: Int,
    val maxTime: Long,
    val list: List<FocusedLogOrBreak>
)