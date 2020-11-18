package com.alaeri.command.core

import com.alaeri.command.AbstractCommandLogger
import com.alaeri.command.CommandState

typealias ICommandLogger<R> = AbstractCommandLogger<CommandState<R>>