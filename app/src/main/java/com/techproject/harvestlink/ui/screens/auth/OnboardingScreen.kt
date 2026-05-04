package com.techproject.harvestlink.ui.screens.auth

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.with
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

// Onboarding page data
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
    OnboardingPage(
        mainTitle = "HarvestLink",
        subtitle = "FROM SOIL TO SALE",
        featureTitle = "Fresh Daily Harvest",
        featureDescription = "Browse hundreds of fresh vegetables, fruits, and grains harvested daily just for you.",
        trustTitle = "Support Local Farmers",
        trustDescription = "Every purchase directly supports small-scale farmers and sustainable agriculture."
    ),
    OnboardingPage(
        mainTitle = "HarvestLink",
        subtitle = "FROM SOIL TO SALE",
        featureTitle = "Seamless Delivery",
        featureDescription = "Get your orders delivered right to your doorstep with real-time order tracking.",
        trustTitle = "Secure Payments",
        trustDescription = "Multiple payment options with 100% secure and protected transactions."
    )
)

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun OnboardingScreen(
    onFinish: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { onboardingPages.size })
    val coroutineScope = rememberCoroutineScope()

    // Centralized animation duration for consistency
    val animationDuration = 600

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Top Bar / Skip Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .height(48.dp),
            contentAlignment = Alignment.CenterEnd
        ) {
            if (pagerState.currentPage < onboardingPages.size - 1) {
                TextButton(onClick = onFinish) {
                    Text(
                        text = "Skip",
                        color = Color.Gray,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Animated Pager
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) { page ->

            // Calculate offset for parallax and alpha animations
            val pageOffset = ((pagerState.currentPage - page) + pagerState.currentPageOffsetFraction).absoluteValue

            OnboardingPageContent(
                page = onboardingPages[page],
                pageOffset = pageOffset,
                modifier = Modifier.fillMaxSize()
            )
        }

        // Smooth Page Indicators (Dots)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(onboardingPages.size) { index ->
                val isSelected = pagerState.currentPage == index

                // Slowed down the dot transitions to match the new page scroll speed
                val width by animateDpAsState(
                    targetValue = if (isSelected) 28.dp else 8.dp,
                    animationSpec = tween(
                        durationMillis = animationDuration,
                        easing = FastOutSlowInEasing
                    ),
                    label = "width"
                )
                val color by animateColorAsState(
                    targetValue = if (isSelected) Color(0xFF1B3D2F) else Color.LightGray.copy(alpha = 0.5f),
                    animationSpec = tween(
                        durationMillis = animationDuration,
                        easing = FastOutSlowInEasing
                    ),
                    label = "color"
                )

                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .height(8.dp)
                        .width(width)
                        .clip(CircleShape)
                        .background(color)
                )
            }
        }

        // Animated Bottom Button
        val isLastPage = pagerState.currentPage == onboardingPages.size - 1

        Button(
            onClick = {
                if (isLastPage) {
                    onFinish()
                } else {
                    coroutineScope.launch {
                        // Added custom animationSpec for a much smoother, slower glide
                        pagerState.animateScrollToPage(
                            page = pagerState.currentPage + 1,
                            animationSpec = tween(
                                durationMillis = animationDuration,
                                easing = FastOutSlowInEasing
                            )
                        )
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .padding(bottom = 16.dp)
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF1B3D2F)
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 4.dp,
                pressedElevation = 8.dp
            )
        ) {
            AnimatedContent(
                targetState = isLastPage,
                transitionSpec = {
                    // Slightly softened the button text transition as well
                    (fadeIn(tween(300)) + slideInVertically { height -> height / 2 }) with
                            (fadeOut(tween(300)) + slideOutVertically { height -> -height / 2 })
                }, label = "buttonText"
            ) { targetIsLast ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = if (targetIsLast) "Get Started" else "Next",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    if (!targetIsLast) {
                        Spacer(modifier = Modifier.width(8.dp))
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
}

@Composable
fun OnboardingPageContent(
    page: OnboardingPage,
    pageOffset: Float,
    modifier: Modifier = Modifier
) {
    // Math to calculate scale and alpha based on scroll position
    val contentAlpha = lerp(start = 0.2f, stop = 1f, fraction = 1f - pageOffset.coerceIn(0f, 1f))
    val illustrationScale = lerp(start = 0.8f, stop = 1f, fraction = 1f - pageOffset.coerceIn(0f, 1f))

    Column(
        modifier = modifier
            .padding(horizontal = 24.dp)
            .fillMaxWidth()
            .graphicsLayer {
                alpha = contentAlpha
            },
        horizontalAlignment = Alignment.Start
    ) {
        // Logo/Icon area
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF1B3D2F).copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "🌾", fontSize = 28.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = page.mainTitle,
            fontSize = 36.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF1B3D2F),
            letterSpacing = (-0.5).sp
        )

        Text(
            text = page.subtitle,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF1B3D2F).copy(alpha = 0.6f),
            letterSpacing = 2.sp,
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Elevated Illustration Card with Parallax Scale
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .graphicsLayer {
                    scaleX = illustrationScale
                    scaleY = illustrationScale
                },
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFFF0F5F2), Color(0xFFE2EBE5))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "🌱", fontSize = 56.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Illustration",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF1B3D2F).copy(alpha = 0.5f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Feature items
        FeatureItem(
            emoji = "📄",
            title = page.featureTitle,
            description = page.featureDescription
        )

        Spacer(modifier = Modifier.height(24.dp))

        FeatureItem(
            emoji = "✓",
            title = page.trustTitle,
            description = page.trustDescription,
            emojiColor = Color(0xFF4CAF50)
        )
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
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(emojiColor.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = emoji, fontSize = 20.sp)
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                fontSize = 14.sp,
                color = Color.Gray,
                lineHeight = 22.sp
            )
        }
    }
}