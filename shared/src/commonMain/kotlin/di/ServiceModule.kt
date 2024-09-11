package di

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import services.ChatService

val serviceModule = module {
    singleOf(::ChatService)
}