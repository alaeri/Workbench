package com.alaeri.command.core.root

import com.alaeri.command.CommandNomenclature
import com.alaeri.command.ICommandLogger

fun buildRootCommandScope(any: Any,
                          name: String? = null,
                          nomenclature: CommandNomenclature = CommandNomenclature.Undefined,
                          iCommandLogger: ICommandLogger
): DefaultRootCommandScope = RootCommandScope(any, name, nomenclature, iCommandLogger)