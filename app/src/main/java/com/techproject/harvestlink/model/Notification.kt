package com.techproject.harvestlink.model

import kotlinx.serialization.Serializable

@Serializable
data class Notification(
    val id: String = "",
    val title: String = "",
    val message: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val type: NotificationType = NotificationType.ORDER
)

@Serializable
enum class NotificationType {
    ORDER, CHAT, PROMO
}
