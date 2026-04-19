package com.techproject.harvestlink

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

// Onboarding page data matching your Figma
data class OnboardingPage(
    val mainTitle: String,
    val subtitle: String,
    val featureTitle: String,
    val featureDescription: String,
    val trustTitle: String,
    val trustDescription: String
)

val onboardingPages = listOf(
    OnboardingPage(
        mainTitle = "HarvestLink",
        subtitle = "FROM SOIL TO SALE",
        featureTitle = "Farm to Table, No Middlemen",
        featureDescription = "Connect directly with local farmers and get the freshest produce at fair prices.",
        trustTitle = "Trusted & Transparent",
        trustDescription = "Verified farmers, real-time listings, and secure transactions you can count on."
    ),
    // Page 2 - You can customize this based on your second splash page
    OnboardingPage(
        mainTitle = "HarvestLink",
        subtitle = "FROM SOIL TO SALE",
        featureTitle = "Fresh Daily Harvest",
        featureDescription = "Browse hundreds of fresh vegetables, fruits, and grains harvested daily just for you.",
        trustTitle = "Support Local Farmers",
        trustDescription = "Every purchase directly supports small-scale farmers and sustainable agriculture."
    ),
    // Page 3
    OnboardingPage(
        mainTitle = "HarvestLink",
        subtitle = "FROM SOIL TO SALE",
        featureTitle = "Seamless Delivery",
        featureDescription = "Get your orders delivered right to your doorstep with real-time order tracking.",
        trustTitle = "Secure Payments",
        trustDescription = "Multiple payment options with 100% secure and protected transactions."
    )
)

@Composable
fun OnboardingScreen(
    onFinish: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { onboardingPages.size })
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Skip button (hidden on last page)
        if (pagerState.currentPage < onboardingPages.size - 1) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                TextButton(
                    onClick = onFinish,
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Text(
                        text = "Skip",
                        color = Color.Gray,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        } else {
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Pager content
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) { page ->
            OnboardingPageContent(
                page = onboardingPages[page],
                modifier = Modifier.fillMaxSize()
            )
        }

        // Page indicators (dots)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(onboardingPages.size) { index ->
                val isSelected = pagerState.currentPage == index
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(if (isSelected) 24.dp else 8.dp)
                        .clip(CircleShape)
                        .background(
                            if (isSelected)
                                Color(0xFF1B3D2F)
                            else
                                Color.LightGray.copy(alpha = 0.5f)
                        )
                )
            }
        }

        // Next/Get Started button
        val isLastPage = pagerState.currentPage == onboardingPages.size - 1

        Button(
            onClick = {
                if (isLastPage) {
                    onFinish()
                } else {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF1B3D2F)
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = if (isLastPage) "Get Started" else "Next",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                if (!isLastPage) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "→",
                        fontSize = 20.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun OnboardingPageContent(
    page: OnboardingPage,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(horizontal = 24.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        // Logo/Icon area
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF1B3D2F)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "🌾",
                fontSize = 30.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Main Title
        Text(
            text = page.mainTitle,
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1B3D2F)
        )

        // Subtitle
        Text(
            text = page.subtitle,
            fontSize = 14.sp,
            color = Color.Gray,
            letterSpacing = 2.sp,
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Illustration placeholder (matches your Figma)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFF5F5F5)
            )
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                // Replace with actual image when available
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "🌱",
                        fontSize = 48.sp
                    )
                    Text(
                        text = "Illustration",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // HarvestLink FROM SOIL TO SALE
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "HarvestLink",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1B3D2F)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "FROM SOIL TO SALE",
                fontSize = 10.sp,
                color = Color.Gray,
                letterSpacing = 1.sp
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Feature section
        FeatureItem(
            emoji = "📄",
            title = page.featureTitle,
            description = page.featureDescription
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Trust section
        FeatureItem(
            emoji = "✓",
            title = page.trustTitle,
            description = page.trustDescription,
            emojiColor = Color(0xFF4CAF50)
        )

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun FeatureItem(
    emoji: String,
    title: String,
    description: String,
    emojiColor: Color = Color(0xFF1B3D2F)
) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Emoji/Icon circle
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(emojiColor.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = emoji,
                fontSize = 20.sp
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1B3D2F)
            )
            Text(
                text = description,
                fontSize = 14.sp,
                color = Color.Gray,
                lineHeight = 20.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}