package com.alaeri.command.core

import com.alaeri.command.CommandState
import com.alaeri.command.di.AbstractCommandLogger

interface ICommandLogger<R> : AbstractCommandLogger<CommandState<R>>