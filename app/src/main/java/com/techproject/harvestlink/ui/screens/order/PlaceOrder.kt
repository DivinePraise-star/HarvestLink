package com.techproject.harvestlink.ui.screens.order

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.techproject.harvestlink.R
import com.techproject.harvestlink.model.Order
import com.techproject.harvestlink.model.OrderItem
import com.techproject.harvestlink.model.Produce
import com.techproject.harvestlink.ui.HarvestViewModel
import com.techproject.harvestlink.ui.ScreenHeader
import com.techproject.harvestlink.ui.theme.HarvestLinkTheme
import java.util.Locale

@Composable
fun PlaceOrderScreen(
    harvestViewModel: HarvestViewModel,
    farmerId: String?,
    onAddProduct: () -> Unit = {},
    onSuccess: (Long) -> Unit,
) {
    val orderViewModel: OrderViewModel = viewModel()
    val homeUiState = harvestViewModel.homeUiState
    val orderItems = remember { mutableStateListOf<OrderItem>().apply { addAll(listOf(OrderItem(homeUiState.currentProduce,1))) } }

    fun updateQuantity(produceId: String, newQuantity: Int) {
        val index = orderItems.indexOfFirst {it.product.id == produceId }
        if (index != -1 && newQuantity in 1..orderItems[index].product.availableQuantity.toInt()) {
            orderItems[index] = orderItems[index].copy(quantity = newQuantity)
        }
    }

    var isLoading by remember { mutableStateOf(false) }
    var address by remember { mutableStateOf(harvestViewModel.buyerProfile.deliveryAddress) }
    var instructions by remember { mutableStateOf("") }
    val deliveryFee = 5000.0

    val subtotal = orderItems.sumOf { it.product.price * it.quantity }
    val grandTotal = subtotal + deliveryFee
    val isOrderValid = address?.isNotBlank() == true && orderItems.isNotEmpty()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            ScreenHeader(
                title = stringResource(R.string.place_order),
                modifier = Modifier.padding(12.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Column(modifier = Modifier.padding(horizontal = 18.dp)) {
                LazyColumn {
                    items(orderItems, key = { it.product.id }) { orderItem ->
                        ProduceCard(
                            produce = orderItem.product,
                            quantity = orderItem.quantity,
                            onQuantityChange = { newQuantity ->
                                updateQuantity(orderItem.product.id, newQuantity)
                            }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = onAddProduct,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFE8F5E9),
                            contentColor = Color(0xFF1B3D2F)
                        ),
                        modifier = Modifier
                            .height(48.dp)
                            .fillMaxWidth(),
                        elevation = ButtonDefaults.buttonElevation(0.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Add, null, Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Add Another Product", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Subtotal row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(stringResource(R.string.subtotal), color = MaterialTheme.colorScheme.onPrimaryContainer, fontSize = 14.sp)
                            Text("UGX ${String.format(Locale.US, "%,d", subtotal.toInt())}", fontWeight = FontWeight.Medium, fontSize = 14.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(stringResource(R.string.delivery_fee), color = MaterialTheme.colorScheme.onPrimaryContainer, fontSize = 14.sp)
                            Text("UGX ${String.format(Locale.US, "%,d", deliveryFee.toInt())}", fontWeight = FontWeight.Medium, fontSize = 14.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Divider(color = MaterialTheme.colorScheme.outline, thickness = 1.dp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(stringResource(R.string.total), fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
                            Text("UGX ${String.format(Locale.US, "%,d", grandTotal.toInt())}", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(stringResource(R.string.delivery_address), fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF1B3D2F))
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = address ?: "",
                    onValueChange = { address = it },
                    placeholder = { Text(stringResource(R.string.delivery_address_placeholder)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF1B3D2F),
                        unfocusedBorderColor = Color.Gray
                    ),
                    singleLine = false,
                    minLines = 2
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(stringResource(R.string.special_instructions), fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF1B3D2F))
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = instructions,
                    onValueChange = { instructions = it },
                    placeholder = { Text(stringResource(R.string.special_instructions_placeholder)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF1B3D2F),
                        unfocusedBorderColor = Color.Gray
                    ),
                    singleLine = false,
                    minLines = 3
                )

                Spacer(modifier = Modifier.height(100.dp))
            }
        }

        Button(
            onClick = {
                if (isOrderValid) {
                    isLoading = true
                    val order = Order(
                        buyerId = harvestViewModel.buyerProfile.id,
                        farmerId = farmerId ?: "",
                        subtotal = 0.0,
                        deliveryFee = deliveryFee,
                        totalAmount = grandTotal,
                    )
                    orderViewModel.placeOrder(order,orderItems, onSuccess = onSuccess)
                }
            },
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B3D2F)),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp)
                .height(56.dp),
            enabled = isOrderValid
        ) {
            if (isLoading){
                CircularProgressIndicator(
                    strokeWidth = 3.dp,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(24.dp))
            }else{
                Text(
                    "Place Order • UGX ${String.format(Locale.US, "%,d", grandTotal.toInt())}",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
fun ProduceCard(
    produce: Produce,
    quantity: Int,
    onQuantityChange: (Int) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(produce.imageUrl)
                .crossfade(true)
                .build(),
            contentDescription = null,
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(12.dp)),
            error = painterResource(R.drawable.placeholder)
        )

        Column(modifier = Modifier
            .weight(1f)
            .padding(start = 12.dp)) {
            Text(produce.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1B3D2F))
            Spacer(modifier = Modifier.height(4.dp))
            Text("UGX ${String.format(Locale.US, "%,d", produce.price.toInt())} per ${produce.unit}", fontSize = 14.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(4.dp))
            Text("${produce.availableQuantity.toInt()} ${produce.unit} available", fontSize = 12.sp, color = Color.Gray)
        }

        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            IconButton(
                onClick = { if (quantity > 1) onQuantityChange(quantity - 1) },
                modifier = Modifier
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondary)
                    .size(36.dp)
            ) {
                Icon(Icons.Default.Remove, null, tint = MaterialTheme.colorScheme.onSecondary)
            }
            Text("$quantity", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1B3D2F))
            IconButton(
                onClick = { if (quantity < produce.availableQuantity.toInt()) onQuantityChange(quantity + 1) },
                modifier = Modifier
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .size(36.dp)
            ) {
                Icon(Icons.Default.Add, null, tint = MaterialTheme.colorScheme.onPrimary)
            }
        }
    }
}

@Composable
fun Divider(color: Color, thickness: androidx.compose.ui.unit.Dp) {
    HorizontalDivider(color = color, thickness = thickness)
}

@Preview(showBackground = true)
@Composable
fun PreviewPlaceOrderScreen() {

}