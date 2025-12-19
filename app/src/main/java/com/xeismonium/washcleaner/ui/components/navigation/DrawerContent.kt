package com.xeismonium.washcleaner.ui.components.navigation

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.xeismonium.washcleaner.ui.navigation.NavigationItem

@Composable
fun DrawerContent(
    currentRoute: String?,
    onItemClick: (NavigationItem) -> Unit
) {
    ModalDrawerSheet {
        Spacer(Modifier.height(12.dp))
        
        Text(
            text = "WashCleaner",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(horizontal = 28.dp, vertical = 24.dp),
            color = MaterialTheme.colorScheme.primary
        )

        NavigationItem.allItems.forEach { item ->
            NavigationDrawerItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title) },
                selected = currentRoute == item.route,
                onClick = { onItemClick(item) },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
            )
        }
    }
}
