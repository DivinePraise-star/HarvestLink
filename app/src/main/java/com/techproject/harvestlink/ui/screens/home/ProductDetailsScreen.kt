package com.techproject.harvestlink.ui.screens.home

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.techproject.harvestlink.R
import com.techproject.harvestlink.ui.theme.HarvestLinkTheme

@Composable
fun ProductDetailsScreen(
    productName: String,
    productPrice: String,
    productStatus: String,
    availableStock: Int,
    farmerLocation: String,
    productDescription: String? = null,
    //Sellers details(to be replaced with a user object)
    sellerName: String,
    sellerLocation: String,
    sellerRating: Double,
    @DrawableRes sellerProfilePic: Int
){
    Column{
        ImageHeader(
            productStatus = productStatus,
            modifier = Modifier
                .height(400.dp)
                .clip(
                    RoundedCornerShape(
                        bottomStart = 24.dp,
                        bottomEnd = 24.dp
                    )
                )
        )
        Column(
            modifier = Modifier.padding(12.dp)
        ){
            Text(
                text = productName,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            Row(
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Text(
                    text = productPrice,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "per kg",
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
            Text(
                text = "${availableStock}kg available",
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            Row(
                modifier = Modifier
                    .padding(bottom = 12.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(8.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.location),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier
                        .padding(end = 4.dp)
                        .size(24.dp)
                )
                Text(
                    text = farmerLocation,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Column(
                modifier = Modifier.padding(bottom = 12.dp)
            ){
                Text(
                    text = "Description",
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = productDescription ?: "Product Description Available",
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
            SellerContainer(
                sellerName = sellerName,
                sellerLocation = sellerLocation,
                sellerRating = sellerRating,
                sellerProfilePic = sellerProfilePic
            )
        }

    }
}

@Composable
fun ImageHeader(
    productStatus: String,
    modifier: Modifier = Modifier
){
    Box(modifier = modifier) {
        Image(
            painter = painterResource(R.drawable.tomatos),
            contentDescription = null,
            contentScale = ContentScale.Crop
        )
        Box (
            modifier = Modifier
                .matchParentSize()
                .padding(12.dp)
        ){
            IconButton(
                onClick = {},
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier
                    .clip(CircleShape)
            ) {
                Icon(
                    painter = painterResource(R.drawable.backbutton),
                    contentDescription = stringResource(R.string.upButton),
                    modifier = Modifier.size(28.dp)
                )
            }
            Text(
                text = productStatus,
                fontWeight = FontWeight.Bold,
                color = if(productStatus == "Available") MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .clip(RoundedCornerShape(50))
                    .background(if(productStatus == "Available") MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onErrorContainer)
                    .padding(8.dp)
            )
        }
    }
}

@Composable
fun SellerContainer(
    sellerName: String,
    sellerRating: Double,
    sellerLocation: String,
    @DrawableRes sellerProfilePic: Int
){
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(18.dp)
    ) {
        Text(
            text = stringResource(R.string.seller),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(sellerProfilePic),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .padding(end = 12.dp)
                    .width(72.dp)
                    .aspectRatio(1f)
                    .clip(CircleShape)
            )
            Column {
                Text(
                    text = sellerName,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Row {
                    Row {
                        Icon(
                            painter = painterResource(R.drawable.star_rating),
                            contentDescription = null,
                            tint = Color(0xFFFFD700),
                            modifier = Modifier
                                .padding(end = 4.dp)
                                .size(24.dp)
                        )
                        Text(
                            text = sellerRating.toString(),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Row {
                        Icon(
                            painter = painterResource(R.drawable.location),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier
                                .padding(end = 4.dp)
                                .size(24.dp)
                        )
                        Text(
                            text = sellerLocation,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProductDetailsPreview(){
    HarvestLinkTheme(darkTheme = false){
        ProductDetailsScreen(
            productName = "Organic Tomatoes",
            productPrice = "Ugx 40,000",
            productStatus = "Available",
            availableStock = 50,
            farmerLocation = "Mbarara, Uganda",
            productDescription = "Fresh organic tomatoes, harvested this morning. Ri..",
            sellerName = "Kirabo Eria",
            sellerLocation = "Mbarara, Uganda",
            sellerRating = 4.6,
            sellerProfilePic = R.drawable.tomatos
        )
    }
}