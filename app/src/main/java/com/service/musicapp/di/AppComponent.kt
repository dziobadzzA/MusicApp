package com.service.musicapp.di

import dagger.Component

@Component(modules = [ViewModuleConstructor::class])
interface AppComponent {
    fun viewFactory():ViewModelFactory
}