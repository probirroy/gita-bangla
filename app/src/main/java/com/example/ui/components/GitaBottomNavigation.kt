package com.example.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.AppScreen

@Composable
fun GitaBottomNavigation(
    currentScreen: AppScreen,
    onNavigate: (AppScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier.fillMaxWidth(),
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        val navColors = NavigationBarItemDefaults.colors(
            selectedIconColor = MaterialTheme.colorScheme.primary,
            selectedTextColor = MaterialTheme.colorScheme.primary,
            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
        )

        NavigationBarItem(
            selected = currentScreen == AppScreen.HOME,
            onClick = { onNavigate(AppScreen.HOME) },
            icon = {
                Icon(
                    imageVector = if (currentScreen == AppScreen.HOME) Icons.Filled.Home else Icons.Outlined.Home,
                    contentDescription = "হোম"
                )
            },
            label = { Text("হোম", fontSize = 11.sp) },
            colors = navColors,
            modifier = Modifier.testTag("nav_home")
        )

        NavigationBarItem(
            selected = currentScreen == AppScreen.CHAPTER_LIST || currentScreen == AppScreen.CHAPTER_DETAIL,
            onClick = { onNavigate(AppScreen.CHAPTER_LIST) },
            icon = {
                Icon(
                    imageVector = if (currentScreen == AppScreen.CHAPTER_LIST || currentScreen == AppScreen.CHAPTER_DETAIL) Icons.Filled.AutoStories else Icons.Outlined.AutoStories,
                    contentDescription = "অধ্যায়"
                )
            },
            label = { Text("অধ্যায়", fontSize = 11.sp) },
            colors = navColors,
            modifier = Modifier.testTag("nav_chapters")
        )

        NavigationBarItem(
            selected = currentScreen == AppScreen.BOOKMARKS,
            onClick = { onNavigate(AppScreen.BOOKMARKS) },
            icon = {
                Icon(
                    imageVector = if (currentScreen == AppScreen.BOOKMARKS) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                    contentDescription = "প্রিয় শ্লোক"
                )
            },
            label = { Text("প্রিয় শ্লোক", fontSize = 11.sp) },
            colors = navColors,
            modifier = Modifier.testTag("nav_bookmarks")
        )

        NavigationBarItem(
            selected = currentScreen == AppScreen.REFLECTIONS,
            onClick = { onNavigate(AppScreen.REFLECTIONS) },
            icon = {
                Icon(
                    imageVector = if (currentScreen == AppScreen.REFLECTIONS) Icons.Filled.EditNote else Icons.Outlined.EditNote,
                    contentDescription = "চিন্তন"
                )
            },
            label = { Text("চিন্তন", fontSize = 11.sp) },
            colors = navColors,
            modifier = Modifier.testTag("nav_reflections")
        )

        NavigationBarItem(
            selected = currentScreen == AppScreen.SEARCH,
            onClick = { onNavigate(AppScreen.SEARCH) },
            icon = {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "সন্ধান"
                )
            },
            label = { Text("সন্ধান", fontSize = 11.sp) },
            colors = navColors,
            modifier = Modifier.testTag("nav_search")
        )

        NavigationBarItem(
            selected = currentScreen == AppScreen.SETTINGS,
            onClick = { onNavigate(AppScreen.SETTINGS) },
            icon = {
                Icon(
                    imageVector = if (currentScreen == AppScreen.SETTINGS) Icons.Filled.Settings else Icons.Outlined.Settings,
                    contentDescription = "সেটিংস"
                )
            },
            label = { Text("সেটিংস", fontSize = 11.sp) },
            colors = navColors,
            modifier = Modifier.testTag("nav_settings")
        )
    }
}
