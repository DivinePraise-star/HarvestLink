package com.techproject.harvestlink.ui.screens.order

//
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material3.*
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//
//@Composable
//fun OrdersScreen() {
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Color(0xFFFBF8F3)) // Matches the theme background
//            .padding(16.dp)
//    ) {
//        Text(
//            text = "My Orders",
//            style = MaterialTheme.typography.headlineMedium,
//            fontWeight = FontWeight.Bold,
//            color = Color(0xFF1B3D2F),
//            modifier = Modifier.padding(bottom = 16.dp)
//        )
//
//        LazyColumn(
//            verticalArrangement = Arrangement.spacedBy(12.dp),
//            contentPadding = PaddingValues(bottom = 80.dp)
//        ) {
//            items(sampleOrders) { order ->
//                OrderCard(order)
//            }
//        }
//    }
//}
//
//@Composable
//fun OrderCard(order: Order) {
//    Card(
//        modifier = Modifier.fillMaxWidth(),
//        shape = RoundedCornerShape(16.dp),
//        colors = CardDefaults.cardColors(containerColor = Color.White),
//        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
//    ) {
//        Column(
//            modifier = Modifier.padding(16.dp)
//        ) {
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceBetween,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Column {
//                    Text(
//                        text = order.produce.name,
//                        fontWeight = FontWeight.Bold,
//                        fontSize = 18.sp,
//                        color = Color(0xFF1B3D2F)
//                    )
//                    Text(
//                        text = "Order ID: ${order.id}",
//                        fontSize = 12.sp,
//                        color = Color.Gray
//                    )
//                }
//                StatusBadge(order.status)
//            }
//
//            Divider(modifier = Modifier.padding(vertical = 12.dp), thickness = 0.5.dp, color = Color.LightGray)
//
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceBetween
//            ) {
//                Column {
//                    Text(text = "Farmer", fontSize = 12.sp, color = Color.Gray)
//                    Text(text = order.produce.farmer.name, fontWeight = FontWeight.Medium)
//                }
//                Column(horizontalAlignment = Alignment.End) {
//                    Text(text = "Date", fontSize = 12.sp, color = Color.Gray)
//                    Text(text = order.orderDate, fontWeight = FontWeight.Medium)
//                }
//            }
//
//            Spacer(modifier = Modifier.height(12.dp))
//
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceBetween,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Column {
//                    Text(text = "Quantity", fontSize = 12.sp, color = Color.Gray)
//                    Text(text = "${order.quantity} ${order.produce.unit}", fontWeight = FontWeight.Medium)
//                }
//                Column(horizontalAlignment = Alignment.End) {
//                    Text(text = "Total Price", fontSize = 12.sp, color = Color.Gray)
//                    Text(
//                        text = "$${order.totalPrice.toInt()}",
//                        fontWeight = FontWeight.Bold,
//                        fontSize = 18.sp,
//                        color = Color(0xFF1B3D2F)
//                    )
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun StatusBadge(status: OrderStatus) {
//    val (backgroundColor, textColor) = when (status) {
//        OrderStatus.PENDING -> Color(0xFFFFF3E0) to Color(0xFFE65100)
//        OrderStatus.PROCESSING -> Color(0xFFE3F2FD) to Color(0xFF1565C0)
//        OrderStatus.DELIVERED -> Color(0xFFE8F5E9) to Color(0xFF2E7D32)
//        OrderStatus.CANCELLED -> Color(0xFFFFEBEE) to Color(0xFFC62828)
//    }
//
//    Surface(
//        color = backgroundColor,
//        shape = RoundedCornerShape(8.dp)
//    ) {
//        Text(
//            text = status.name,
//            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
//            fontSize = 10.sp,
//            fontWeight = FontWeight.Bold,
//            color = textColor
//        )
//    }
//}
