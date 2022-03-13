package nick.template.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.Dispatchers
import nick.template.usecase.AndroidAppScopedFilesystem
import nick.template.usecase.Filesystem

@Module
@InstallIn(SingletonComponent::class)
interface AppModule {
    companion object {
        @Provides
        fun ioContext(): CoroutineContext = Dispatchers.IO
    }

    @Binds
    fun filesystem(filesystem: AndroidAppScopedFilesystem): Filesystem
}
