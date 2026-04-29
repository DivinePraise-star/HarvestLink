package com.techproject.harvestlink.ui.screens.order

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.lifecycle.viewmodel.compose.viewModel
import com.techproject.harvestlink.R
import com.techproject.harvestlink.model.Order
import com.techproject.harvestlink.model.OrderDetails
import com.techproject.harvestlink.model.OrderStatus
import com.techproject.harvestlink.ui.ScreenHeader
import com.techproject.harvestlink.ui.theme.HarvestLinkTheme

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
        val selectedOrder = orderUIState.selectedOrder
        if (selectedOrder != null) {
            OrderDetails(
                order = selectedOrder,
                orderUiState = orderUIState,
                onClick = {
                    orderViewModel.toggleOrderDetails()
                }
            )
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("No order selected", color = Color.Red)
                Button(onClick = { orderViewModel.toggleOrderDetails() }) {
                    Text("Back to Orders")
                }
            }
        }
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
        if(orderUIState.orders.isEmpty()){
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) { CircularProgressIndicator() }
        }else{
            LazyColumn {
                orderUIState.orders.forEach { (order, details) ->
                    item{
                        OrderListItem(
                            item = order,
                            items = details,
                            onClick = {
                                orderViewModel.showOrderDetails(order)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OrderListItem(
    item: Order,
    items: List<OrderDetails>,
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
            .clickable {
                onClick()
            }

    ) {
        Column {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
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
                }
                Text(
                    text = item.orderStatus.text,
                    fontWeight = FontWeight.Bold,
                    color = when(item.orderStatus.text){
                        OrderStatus.delivered.text ->{
                            MaterialTheme.colorScheme.onPrimaryContainer
                        }
                        OrderStatus.in_transit.text ->{
                            if(isSystemInDarkTheme()) Color(0xFFDBEAFE) else Color(0xFF193CB8)
                        }
                        OrderStatus.pending.text ->{
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
                                OrderStatus.delivered.text -> {
                                    MaterialTheme.colorScheme.primaryContainer
                                }

                                OrderStatus.in_transit.text -> {
                                    if (isSystemInDarkTheme()) Color(0xFF193CB8) else Color(0xFFDBEAFE)
                                }

                                OrderStatus.pending.text -> {
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
            Column(
                modifier = Modifier
                    .padding(top = 8.dp)
            ){
                items.forEach { item ->
                    Text(
                        text = "${item.produceName} (${item.quantity} ${item.produceUnit})",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
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
            Text(
                text = "UGX ${items.sumOf { it.producePrice?.times(it.quantity) ?: 0.0 }}",
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = item.farmerName,
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
    orderUiState: OrderUiState,
    onClick: () -> Unit
) {
    val orderDetails = orderUiState.orders[order]
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
            modifier = Modifier
                .padding(12.dp)
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ){
            if(order.orderStatus != OrderStatus.cancelled){
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
                        Text(
                            text = "Farmer",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Text(
                            text = order.farmerName,
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

                orderDetails?.forEach {item ->
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    ) {
                        Column {
                            Text(
                                text = item.produceName ?: "Unknown",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${item.quantity} ${item.produceUnit}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                        }
                        Text(
                            text = "UGX ${item.producePrice?.times(item.quantity)}",
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
                        text = "UGX ${orderDetails?.sumOf{ it.producePrice?.times(it.quantity) ?: 0.0 }}",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }
        Row(
            modifier = Modifier.padding(12.dp)
        ){
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
                            OrderStatus.delivered, OrderStatus.in_transit, OrderStatus.pending -> {
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
                    OrderStatus.delivered, OrderStatus.in_transit, OrderStatus.pending -> {
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
                            OrderStatus.delivered, OrderStatus.in_transit, OrderStatus.pending -> {
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
                            OrderStatus.delivered, OrderStatus.in_transit -> {
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
                    OrderStatus.delivered, OrderStatus.in_transit -> {
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
                            OrderStatus.delivered, OrderStatus.in_transit -> {
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
                            OrderStatus.delivered -> {
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
                    OrderStatus.delivered -> {
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
                            OrderStatus.delivered -> {
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
                            OrderStatus.delivered -> {
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
            Text(OrderStatus.pending.text)
            Text(OrderStatus.in_transit.text)
            Text(OrderStatus.delivered.text)
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
            text = OrderStatus.cancelled.name,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.error
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TrackOrderScreenPreview(){
    HarvestLinkTheme(darkTheme = false) {
        //TrackOrderScreen()
    }
}
