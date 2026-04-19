package com.techproject.harvestlink.ui.screens.order

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.techproject.harvestlink.R
import com.techproject.harvestlink.data.LocalUserData
import com.techproject.harvestlink.data.MoreData
import com.techproject.harvestlink.model.Order
import com.techproject.harvestlink.model.OrderStatus
import com.techproject.harvestlink.ui.HarvestViewModel
import com.techproject.harvestlink.ui.ScreenHeader
import com.techproject.harvestlink.ui.theme.HarvestLinkTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TrackOrderScreen(){
    val orderViewModel: OrderViewModel = viewModel()
    val orderUIState = orderViewModel.orderUiState.collectAsState().value

    if(!orderUIState.showOrderDetails){
        OrderList(
            orderViewModel =  orderViewModel,
            orderUIState = orderUIState
        )
    }else{
        OrderDetails(
            order = orderUIState.selectedOrder,
            onClick = {
                orderViewModel.toggleOrderDetails()
            }
        )
    }

}

@Composable
fun OrderList(
    orderViewModel: OrderViewModel,
    orderUIState: OrderUiState
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ){
        ScreenHeader(
            title = stringResource(R.string.orderScreenHeader),
            modifier = Modifier
                .padding(12.dp)
        )
        LazyColumn {
            items(orderUIState.orders) { item ->
                OrderListItem(
                    item = item,
                    onClick = {
                        orderViewModel.showOrderDetails(item)
                    }
                )
            }
        }
    }
}

@Composable
fun OrderListItem(
    item: Order,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val timeFormatter = remember { SimpleDateFormat("d MMMM yyyy h:mm a", Locale.getDefault()) }
    val timeString = timeFormatter.format(Date(item.orderDate))

    Column(
        modifier = modifier
            .padding(vertical = 8.dp, horizontal = 12.dp)
            .fillMaxWidth()
            .border(
                width = 2.dp,
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            .padding(12.dp)
            .clickable{
                onClick()
            }

    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            Column {
                Text(
                    text = "HL-2026-${item.id}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = timeString,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Column(
                    modifier = Modifier
                        .padding(top = 8.dp)
                ){
                    item.items.forEach { item ->
                        Text(
                            text = "${item.product.name} (${item.quantity} ${item.product.unit})",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Text(
                text = item.orderStatus.text,
                fontWeight = FontWeight.Bold,
                color = when(item.orderStatus.text){
                    OrderStatus.DELIVERED.text ->{
                        MaterialTheme.colorScheme.onPrimaryContainer
                    }
                    OrderStatus.IN_TRANSIT.text ->{
                        if(isSystemInDarkTheme()) Color(0xFFDBEAFE) else Color(0xFF193CB8)
                    }
                    OrderStatus.PENDING.text ->{
                        if(isSystemInDarkTheme()) Color(0xFFFEF9C2) else Color(0xFF894B00)
                    }
                    else ->{
                        MaterialTheme.colorScheme.onErrorContainer
                    }
                },
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(
                        color = when (item.orderStatus.text) {
                            OrderStatus.DELIVERED.text -> {
                                MaterialTheme.colorScheme.primaryContainer
                            }

                            OrderStatus.IN_TRANSIT.text -> {
                                if (isSystemInDarkTheme()) Color(0xFF193CB8) else Color(0xFFDBEAFE)
                            }

                            OrderStatus.PENDING.text -> {
                                if (isSystemInDarkTheme()) Color(0xFF894B00) else Color(0xFFFEF9C2)
                            }

                            else -> {
                                MaterialTheme.colorScheme.errorContainer
                            }
                        }
                    )
                    .padding(8.dp)
            )
        }
        HorizontalDivider(
            color = MaterialTheme.colorScheme.outline,
            modifier = Modifier
                .padding(vertical = 8.dp)
        )
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 2.dp)
        ){
            val userName = remember(item.farmerId) {
                MoreData.farmers.find{ it.id == item.farmerId }?.name ?: "Unknown"
            }
            Text(
                text = "UGX ${ item.items.sumOf {it.quantity * it.product.price}}",
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = userName,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}


/**
 * Order Details Screen
 */
@Composable
fun OrderDetails(
    order: Order,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ){
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)
        ) {
            IconButton(
                onClick = onClick,
                modifier = Modifier
                    .padding(4.dp)
                    .size(24.dp)
                ) {
                Icon(
                    painter = painterResource(R.drawable.backbutton),
                    contentDescription = null
                )
            }
            ScreenHeader(title = "Order Details")
        }

        Column(
            modifier = Modifier.padding(12.dp)
        ){
            if(order.orderStatus != OrderStatus.CANCELLED){
                ProgressIndictor(
                    orderStatus = order.orderStatus,
                    modifier = Modifier.padding(vertical = 24.dp)
                )
            }else{
                CancelledOrder(modifier = Modifier.padding(vertical = 24.dp))
            }

            Column{
                Text(
                    text = "Delivery Information",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Row(
                    modifier = Modifier.padding(vertical = 8.dp)
                ){
                    Icon(
                        painter = painterResource(R.drawable.location),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(end = 4.dp)
                            .size(24.dp)
                    )
                    Column {
                        Text(
                            text = "Delivery Address",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Text(
                            text = "Plot 123, Kampala Road, Kampala",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                Row (
                    modifier = Modifier.padding(bottom = 8.dp)
                ){
                    Icon(
                        painter = painterResource(R.drawable.phone_button),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(end = 4.dp)
                            .size(24.dp)
                    )
                    Column {
                        val userName = remember(order.farmerId) {
                            LocalUserData.sampleUsers.find { it.id == order.farmerId }?.name ?: "Unknown"
                        }
                        Text(
                            text = "Farmer",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Text(
                            text = userName,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Column{
                Text(
                    text = "Order Summary",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ){
                    val timeFormatter = remember { SimpleDateFormat("d MMMM yyyy h:mm a", Locale.getDefault()) }
                    val timeString = timeFormatter.format(Date(order.orderDate))

                    Text(
                        text = "Order Date",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = timeString,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ){

                    Text(
                        text = "Order Number",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = "HL-2026-${order.id}",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Column{
                Text(
                    text = "Order Items",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                order.items.forEach {item ->
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    ) {
                        Column {
                            Text(
                                text = item.product.name,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${item.quantity} ${item.product.unit}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                        }
                        Text(
                            text = "UGX ${item.quantity * item.product.price}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ){
                    Text(
                        text = "Total",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "UGX ${order.items.sumOf {it.quantity * it.product.price}}",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            Row{
                OutlinedButton(
                    shape = RoundedCornerShape(8.dp),
                    onClick = {},
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .weight(1f)
                ) {
                    Text(
                        text = "Contact Farmer"
                    )
                }
                Button(
                    shape = RoundedCornerShape(8.dp),
                    onClick = {},
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Reorder"
                    )
                }

            }
        }
    }
}

@Composable
fun ProgressIndictor(
    orderStatus: OrderStatus,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier){
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            HorizontalDivider(
                color = Color.Transparent,
                modifier = Modifier
                    .height(4.dp)
                    .weight(1f)
                    .background(
                        when (orderStatus) {
                            OrderStatus.DELIVERED, OrderStatus.IN_TRANSIT, OrderStatus.PENDING -> {
                                MaterialTheme.colorScheme.primary
                            }

                            else -> {
                                Color.Gray
                            }
                        }
                    )
            )
            Icon(
                painter = painterResource(R.drawable.clock_waiting),
                tint = when (orderStatus) {
                    OrderStatus.DELIVERED, OrderStatus.IN_TRANSIT, OrderStatus.PENDING -> {
                        MaterialTheme.colorScheme.onPrimary
                    }
                    else -> {
                        Color.Gray
                    }
                },
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .then(
                        when (orderStatus) {
                            OrderStatus.DELIVERED, OrderStatus.IN_TRANSIT, OrderStatus.PENDING -> {
                                Modifier
                                    .background(MaterialTheme.colorScheme.primary)
                                    .padding(6.dp)
                            }

                            else -> {
                                Modifier
                                    .background(Color.Transparent)
                                    .border(4.dp, Color.Gray, CircleShape)
                                    .padding(6.dp)
                            }
                        }
                    )
            )
            HorizontalDivider(
                color = Color.Transparent,
                modifier = Modifier
                    .height(4.dp)
                    .weight(1f)
                    .background(
                        when (orderStatus) {
                            OrderStatus.DELIVERED, OrderStatus.IN_TRANSIT -> {
                                MaterialTheme.colorScheme.primary
                            }

                            else -> {
                                Color.Gray
                            }
                        }
                    )
            )
            Icon(
                painter = painterResource(R.drawable.truck_icon),
                tint = when (orderStatus) {
                    OrderStatus.DELIVERED, OrderStatus.IN_TRANSIT -> {
                        MaterialTheme.colorScheme.onPrimary
                    }

                    else -> {
                        Color.Gray
                    }
                },
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .then(
                        when (orderStatus) {
                            OrderStatus.DELIVERED, OrderStatus.IN_TRANSIT -> {
                                Modifier
                                    .background(MaterialTheme.colorScheme.primary)
                                    .padding(6.dp)
                            }

                            else -> {
                                Modifier
                                    .background(Color.Transparent)
                                    .border(4.dp, Color.Gray, CircleShape)
                                    .padding(6.dp)
                            }
                        }
                    )
            )
            HorizontalDivider(
                color = Color.Transparent,
                modifier = Modifier
                    .height(4.dp)
                    .weight(1f)
                    .background(
                        when (orderStatus) {
                            OrderStatus.DELIVERED -> {
                                MaterialTheme.colorScheme.primary
                            }

                            else -> {
                                Color.Gray
                            }
                        }
                    )
            )
            Icon(
                painter = painterResource(R.drawable.circle_check),
                tint = when (orderStatus) {
                    OrderStatus.DELIVERED -> {
                        MaterialTheme.colorScheme.onPrimary
                    }

                    else -> {
                        Color.Gray
                    }
                },
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .then(
                        when (orderStatus) {
                            OrderStatus.DELIVERED -> {
                                Modifier
                                    .background(MaterialTheme.colorScheme.primary)
                                    .padding(6.dp)
                            }

                            else -> {
                                Modifier
                                    .background(Color.Transparent)
                                    .border(4.dp, Color.Gray, CircleShape)
                                    .padding(6.dp)
                            }
                        }
                    )
            )
            HorizontalDivider(
                color = Color.Transparent,
                modifier = Modifier
                    .height(4.dp)
                    .weight(1f)
                    .background(
                        when (orderStatus) {
                            OrderStatus.DELIVERED -> {
                                MaterialTheme.colorScheme.primary
                            }

                            else -> {
                                Color.Gray
                            }
                        }
                    )
            )
        }
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ){
            Text(OrderStatus.PENDING.text)
            Text(OrderStatus.IN_TRANSIT.text)
            Text(OrderStatus.DELIVERED.text)
        }
    }
}

@Composable
fun CancelledOrder(modifier: Modifier = Modifier){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxWidth()
    ){
        Icon(
            painter = painterResource(R.drawable.circle_xmark),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier
                .size(80.dp)
        )
        Text(
            text = OrderStatus.CANCELLED.name,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.error
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TrackOrderScreenPreview(){
    HarvestLinkTheme(darkTheme = false) {
        TrackOrderScreen()
    }
}


