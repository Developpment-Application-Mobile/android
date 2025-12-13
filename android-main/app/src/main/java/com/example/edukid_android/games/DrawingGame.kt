package com.example.edukid_android.games

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

data class PathState(
    val path: List<Offset>,
    val color: Color,
    val strokeWidth: Float
)

@Composable
fun DrawingGame(navController: NavController) {
    var paths by remember { mutableStateOf<List<PathState>>(emptyList()) }
    var currentPath by remember { mutableStateOf<List<Offset>>(emptyList()) }
    var currentColor by remember { mutableStateOf(Color.Black) }
    var currentStrokeWidth by remember { mutableStateOf(10f) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Toolbar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF9C27B0))
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            
            Text("Drawing Pad", color = Color.White)
            
            IconButton(onClick = { 
                paths = emptyList()
                currentPath = emptyList()
            }) {
                Icon(Icons.Default.Delete, contentDescription = "Clear", tint = Color.White)
            }
        }

        // Canvas
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            currentPath = listOf(offset)
                        },
                        onDrag = { change, dragAmount ->
                            val newPoint = change.position
                            currentPath = currentPath + newPoint
                        },
                        onDragEnd = {
                            if (currentPath.isNotEmpty()) {
                                paths = paths + PathState(currentPath, currentColor, currentStrokeWidth)
                                currentPath = emptyList()
                            }
                        }
                    )
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                // Draw saved paths
                paths.forEach { pathState ->
                    for (i in 0 until pathState.path.size - 1) {
                        drawLine(
                            color = pathState.color,
                            start = pathState.path[i],
                            end = pathState.path[i + 1],
                            strokeWidth = pathState.strokeWidth,
                            cap = StrokeCap.Round
                        )
                    }
                }
                // Draw current path
                if (currentPath.isNotEmpty()) {
                    for (i in 0 until currentPath.size - 1) {
                        drawLine(
                            color = currentColor,
                            start = currentPath[i],
                            end = currentPath[i + 1],
                            strokeWidth = currentStrokeWidth,
                            cap = StrokeCap.Round
                        )
                    }
                }
            }
        }

        // Color Palette
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            val colors = listOf(Color.Black, Color.Red, Color.Blue, Color.Green, Color.Yellow, Color(0xFF9C27B0))
            colors.forEach { color ->
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(color)
                        .border(2.dp, if (currentColor == color) Color.Gray else Color.Transparent, CircleShape)
                        .clickable { currentColor = color }
                )
            }
        }
    }
}
