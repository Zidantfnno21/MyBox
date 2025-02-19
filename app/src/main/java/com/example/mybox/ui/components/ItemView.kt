package com.example.mybox.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.mybox.data.model.CategoryModel

@Composable
fun RegularItemView(item: CategoryModel, onExpand: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onExpand() },
        border = BorderStroke(1.dp, Color.Gray),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(item.imageURL)
                    .crossfade(true)
                    .build(),
                contentDescription = "Network Image",
                modifier = Modifier
                    .fillMaxHeight() // Make it fill the height
                    .width(80.dp) // Keep it from stretching too much
                    .clip(RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp)) // Keep rounded corners
                    .background(Color.Gray, shape = RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp)), // Background color to prevent weird gaps
                placeholder = painterResource(id = android.R.drawable.ic_menu_report_image),
                alignment = Alignment.Center, // Centers error & placeholder image
                contentScale = ContentScale.Fit,
            )
            Spacer(modifier = Modifier.width(12.dp)) // Space between image and text
            Column(
                modifier = Modifier
                    .padding(all = 16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                    ,
                    text = item.name.toString(),
                    fontWeight = FontWeight.Bold,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                )
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                    ,
                    text = item.description.toString(),
                    fontSize = 14.sp,
                    color = Color.Gray,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 2,
                )
            }
        }
    }

}

@Composable
fun ExpandedItemView(item: CategoryModel, onCollapse: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCollapse() }, // Collapse on click
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.LightGray)
    ) {
        Row {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = item.name.toString(), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = item.description.toString())
            }
            IconButton(onClick = onCollapse) {
                Icon(Icons.Filled.ArrowDropUp, contentDescription = "Collapse")
            }
        }
    }
}

@Composable
fun ItemList(items: List<CategoryModel>) {
    var expandedIndex by remember { mutableStateOf<Int?>(null) }

    LazyColumn {
        itemsIndexed(items) { index, item ->
            AnimatedVisibility(visible = expandedIndex == index) {
                ExpandedItemView(item) { expandedIndex = null } // Hide when clicked
            }
            AnimatedVisibility(visible = expandedIndex != index) {
                RegularItemView(item) { expandedIndex = index } // Show expanded when clicked
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewItemView() {
    val dummyItems = listOf(
        CategoryModel(name = "Category 1", description =  "Description for Category 1 Description for Category 1 Description for Category 1 Description for Category 1 Description for Category 1 Description for Category 1"),
        CategoryModel(name ="Category 2", description = "Description for Category 2"),
        CategoryModel(name ="Category 3", description = "Description for Category 3")
    )
    ItemList(dummyItems)
}

/*
TODO:
- advanced search and filter
- reminder for item (check this item / box for next week) && expired / maintenance tracking (food, pc batteries, etc)
- labelling of item (packed , missing , last usage , need repair, new, last seen on... etc),
- carousel for image category
- carousel for per item image in category
- checklist mode??
- duplication detection??

/// based from chatGPT ->
    -Spot place repeatable -> in app feature as Notifications Reminders : "Don't forget to place back ur bla bla in bla bla"
    -Reminders -> in app feature also as NOTIFICATIONS : "its time for ..."
    -Tracking -> Track in place / gps each item
    -Visualizing -> same as reminders
    -Routine -> same as reminders
    -Memory Journal -> writ it down (already done featured)
 */