package com.boycott.app.navigation

sealed class NavigationEvent {
    object ToSearchResults : NavigationEvent()
    data class ToBrandDetail(val brandId: String) : NavigationEvent()
} 