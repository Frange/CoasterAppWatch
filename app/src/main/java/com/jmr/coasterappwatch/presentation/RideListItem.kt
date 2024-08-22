package com.jmr.coasterappwatch.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Text
import com.jmr.coasterappwatch.domain.model.Ride

@Composable
fun RideListItem(ride: Ride, onClick: (Ride) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(ride) }
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = ride.name ?: "-", modifier = Modifier.weight(1f))
        Text(
            text = if (ride.waitTime == null || ride.waitTime < 0) "CLOSED" else ride.waitTime.toString(),
            color = if (ride.waitTime == null || ride.waitTime < 0) Color.Red else Color.Black
        )
    }
}
