package com.techproject.harvestlink.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.techproject.harvestlink.model.Buyer
import com.techproject.harvestlink.model.ConversationDetails
import com.techproject.harvestlink.model.Farmer
import com.techproject.harvestlink.model.Message
import com.techproject.harvestlink.model.MessageStatus
import com.techproject.harvestlink.model.MessageType
import com.techproject.harvestlink.model.OrderDetails
import com.techproject.harvestlink.model.OrderStatus
import com.techproject.harvestlink.model.Produce
import com.techproject.harvestlink.model.User

@Entity(tableName = "buyer_cache")
data class BuyerEntity(
    @PrimaryKey val id: String,
    val name: String,
    val email: String,
    @ColumnInfo(name = "phone_number") val phoneNumber: String,
    @ColumnInfo(name = "is_online") val isOnline: Boolean,
    @ColumnInfo(name = "delivery_address") val deliveryAddress: String?,
    @ColumnInfo(name = "preferred_payment_method") val preferredPaymentMethod: String?,
    @ColumnInfo(name = "order_history_count") val orderHistoryCount: Int
)

@Entity(tableName = "farmer_cache")
data class FarmerEntity(
    @PrimaryKey val id: String,
    val name: String,
    val email: String,
    @ColumnInfo(name = "phone_number") val phoneNumber: String,
    @ColumnInfo(name = "is_online") val isOnline: Boolean,
    val location: String,
    val rating: Double,
    @ColumnInfo(name = "sales_completed") val salesCompleted: Int,
    @ColumnInfo(name = "farm_name") val farmName: String?
)

@Entity(tableName = "produce_cache")
data class ProduceEntity(
    @PrimaryKey val id: String,
    val name: String,
    @ColumnInfo(name = "farmer_name") val farmerName: String,
    val price: Double,
    val unit: String,
    @ColumnInfo(name = "available_quantity") val availableQuantity: Double,
    val rating: Double,
    val description: String,
    @ColumnInfo(name = "harvest_date") val harvestDate: String,
    @ColumnInfo(name = "farmer_id") val farmerId: String,
    @ColumnInfo(name = "image_url") val imageUrl: String?,
    val category: String
)

@Entity(
    tableName = "order_details_cache",
    primaryKeys = ["order_id", "item_id"]
)
data class OrderDetailsEntity(
    @ColumnInfo(name = "order_id") val orderId: Int,
    val status: String,
    @ColumnInfo(name = "ordered_at") val orderedAt: Long,
    @ColumnInfo(name = "delivery_address") val deliveryAddress: String?,
    val currency: String,
    val subtotal: Double,
    @ColumnInfo(name = "delivery_fee") val deliveryFee: Double,
    @ColumnInfo(name = "total_amount") val totalAmount: Double,
    @ColumnInfo(name = "buyer_name") val buyerName: String,
    @ColumnInfo(name = "buyer_id") val buyerId: String,
    @ColumnInfo(name = "buyer_email") val buyerEmail: String,
    @ColumnInfo(name = "farmer_name") val farmerName: String,
    @ColumnInfo(name = "farmer_id") val farmerId: String,
    @ColumnInfo(name = "farmer_email") val farmerEmail: String,
    @ColumnInfo(name = "item_id") val itemId: Int,
    val quantity: Double,
    @ColumnInfo(name = "produce_name") val produceName: String?,
    @ColumnInfo(name = "produce_category") val produceCategory: String?,
    @ColumnInfo(name = "produce_unit") val produceUnit: String?,
    @ColumnInfo(name = "produce_price") val producePrice: Double?
)

@Entity(tableName = "user_cache")
data class UserEntity(
    @PrimaryKey val id: String,
    val name: String,
    val email: String,
    @ColumnInfo(name = "phone_number") val phoneNumber: String,
    @ColumnInfo(name = "is_online") val isOnline: Boolean
)

@Entity(
    tableName = "conversation_cache",
    primaryKeys = ["viewer_id", "conversation_id"]
)
data class ConversationCacheEntity(
    @ColumnInfo(name = "viewer_id") val viewerId: String,
    @ColumnInfo(name = "conversation_id") val conversationId: String,
    @ColumnInfo(name = "user_id") val userId: String,
    @ColumnInfo(name = "user_name") val userName: String,
    @ColumnInfo(name = "last_message_preview") val lastMessagePreview: String?,
    @ColumnInfo(name = "last_message_timestamp") val lastMessageTimestamp: Long,
    @ColumnInfo(name = "unread_count") val unreadCount: Int?
)

@Entity(tableName = "message_cache")
data class MessageEntity(
    @PrimaryKey
    @ColumnInfo(name = "local_id") val localId: String,
    @ColumnInfo(name = "server_id") val serverId: String?,
    @ColumnInfo(name = "conversation_id") val conversationId: String,
    @ColumnInfo(name = "sender_id") val senderId: String,
    val content: String,
    val type: String,
    @ColumnInfo(name = "created_at") val createdAt: Long,
    @ColumnInfo(name = "edited_at") val editedAt: Long?,
    @ColumnInfo(name = "deleted_at") val deletedAt: Long?,
    val status: String,
    @ColumnInfo(name = "reply_to_message_id") val replyToMessageId: String?,
    val metadata: String?
)

fun Buyer.toEntity(): BuyerEntity {
    return BuyerEntity(
        id = id,
        name = name,
        email = email,
        phoneNumber = phoneNumber,
        isOnline = isOnline,
        deliveryAddress = deliveryAddress,
        preferredPaymentMethod = preferredPaymentMethod,
        orderHistoryCount = orderHistoryCount
    )
}

fun BuyerEntity.toModel(): Buyer {
    return Buyer(
        id = id,
        name = name,
        email = email,
        phoneNumber = phoneNumber,
        isOnline = isOnline,
        deliveryAddress = deliveryAddress,
        preferredPaymentMethod = preferredPaymentMethod,
        orderHistoryCount = orderHistoryCount
    )
}

fun Farmer.toEntity(): FarmerEntity {
    return FarmerEntity(
        id = id,
        name = name,
        email = email,
        phoneNumber = phoneNumber,
        isOnline = isOnline,
        location = location,
        rating = rating,
        salesCompleted = salesCompleted,
        farmName = farmName
    )
}

fun FarmerEntity.toModel(): Farmer {
    return Farmer(
        id = id,
        name = name,
        email = email,
        phoneNumber = phoneNumber,
        isOnline = isOnline,
        location = location,
        rating = rating,
        salesCompleted = salesCompleted,
        farmName = farmName
    )
}

fun Produce.toEntity(): ProduceEntity {
    return ProduceEntity(
        id = id,
        name = name,
        farmerName = farmerName,
        price = price,
        unit = unit,
        availableQuantity = availableQuantity,
        rating = rating,
        description = description,
        harvestDate = harvestDate,
        farmerId = farmerId,
        imageUrl = imageUrl,
        category = category
    )
}

fun ProduceEntity.toModel(): Produce {
    return Produce(
        id = id,
        name = name,
        farmerName = farmerName,
        price = price,
        unit = unit,
        availableQuantity = availableQuantity,
        rating = rating,
        description = description,
        harvestDate = harvestDate,
        farmerId = farmerId,
        imageUrl = imageUrl,
        category = category
    )
}

fun OrderDetails.toEntity(): OrderDetailsEntity {
    return OrderDetailsEntity(
        orderId = orderId,
        status = status.name,
        orderedAt = orderedAt,
        deliveryAddress = deliveryAddress,
        currency = currency,
        subtotal = subtotal,
        deliveryFee = deliveryFee,
        totalAmount = totalAmount,
        buyerName = buyerName,
        buyerId = buyerId,
        buyerEmail = buyerEmail,
        farmerName = farmerName,
        farmerId = farmerId,
        farmerEmail = farmerEmail,
        itemId = itemId,
        quantity = quantity,
        produceName = produceName,
        produceCategory = produceCategory,
        produceUnit = produceUnit,
        producePrice = producePrice
    )
}

fun OrderDetailsEntity.toModel(): OrderDetails {
    val resolvedStatus = OrderStatus.values().firstOrNull { it.name == status } ?: OrderStatus.pending
    return OrderDetails(
        orderId = orderId,
        status = resolvedStatus,
        orderedAt = orderedAt,
        deliveryAddress = deliveryAddress,
        currency = currency,
        subtotal = subtotal,
        deliveryFee = deliveryFee,
        totalAmount = totalAmount,
        buyerName = buyerName,
        buyerId = buyerId,
        buyerEmail = buyerEmail,
        farmerName = farmerName,
        farmerId = farmerId,
        farmerEmail = farmerEmail,
        itemId = itemId,
        quantity = quantity,
        produceName = produceName,
        produceCategory = produceCategory,
        produceUnit = produceUnit,
        producePrice = producePrice
    )
}

fun User.toEntity(): UserEntity {
    return UserEntity(
        id = id,
        name = name,
        email = email,
        phoneNumber = phoneNumber,
        isOnline = isOnline
    )
}

fun UserEntity.toModel(): User {
    return User(
        id = id,
        name = name,
        email = email,
        phoneNumber = phoneNumber,
        isOnline = isOnline
    )
}

fun ConversationDetails.toEntity(viewerId: String): ConversationCacheEntity {
    return ConversationCacheEntity(
        viewerId = viewerId,
        conversationId = conversationId,
        userId = userId,
        userName = userName,
        lastMessagePreview = lastMessage,
        lastMessageTimestamp = lastMessageTimestamp,
        unreadCount = unreadCount
    )
}

fun ConversationCacheEntity.toModel(): ConversationDetails {
    return ConversationDetails(
        conversationId = conversationId,
        userName = userName,
        userId = userId,
        lastMessage = lastMessagePreview,
        lastMessageTimestamp = lastMessageTimestamp,
        unreadCount = unreadCount
    )
}

fun Message.toLocalEntity(localId: String, serverId: String? = null): MessageEntity {
    return MessageEntity(
        localId = localId,
        serverId = serverId,
        conversationId = conversationId,
        senderId = senderId,
        content = content,
        type = type.name,
        createdAt = createdAt,
        editedAt = null,
        deletedAt = null,
        status = status.name,
        replyToMessageId = replyToMessageId,
        metadata = metadata
    )
}

fun MessageEntity.toModel(): Message {
    val resolvedType = MessageType.values().firstOrNull { it.name == type } ?: MessageType.text
    val resolvedStatus = MessageStatus.values().firstOrNull { it.name == status } ?: MessageStatus.sending
    return Message(
        id = serverId ?: localId,
        conversationId = conversationId,
        senderId = senderId,
        content = content,
        type = resolvedType,
        createdAt = createdAt,
        status = resolvedStatus,
        replyToMessageId = replyToMessageId,
        metadata = metadata
    )
}
