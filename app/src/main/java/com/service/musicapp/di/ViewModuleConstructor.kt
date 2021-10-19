package com.service.musicapp.di


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.service.musicapp.ui.main.MusicViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap


@Module
abstract class ViewModuleConstructor {

    @Binds
    internal abstract fun bindsViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(MusicViewModel::class)
    abstract fun ViewModel(viewModel: MusicViewModel): ViewModel

}