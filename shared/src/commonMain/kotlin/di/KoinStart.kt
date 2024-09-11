package di

import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin

fun startKoin(): KoinApplication {
    Napier.base(DebugAntilog())
    return startKoin {
        modules(getSharedModules())
    }
}

fun getSharedModules() = listOf(
    configModule,
    apiModule,
    serviceModule
)