package setixx.software.services

import setixx.software.data.dto.*
import setixx.software.data.repositories.*
import setixx.software.utils.dbQuery
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

class OrderService(
    private val orderRepository: OrderRepository,
    private val cartRepository: CartRepository,
    private val productRepository: ProductRepository,
    private val addressRepository: AddressRepository,
    private val deliveryRepository: DeliveryRepository,
    private val userRepository: UserRepository
) {

    suspend fun createOrder(
        userPublicId: String,
        request: CreateOrderRequest
    ): OrderResponse = dbQuery {
        val user = userRepository.findByPublicId(UUID.fromString(userPublicId))
            ?: throw IllegalArgumentException("User not found")

        val address = addressRepository.findAddressById(request.addressId, user.id)
            ?: throw IllegalArgumentException("Address not found")

        val cart = cartRepository.findOrCreateCart(user.id)
        val cartItems = cartRepository.getCartItems(cart.id)

        if (cartItems.isEmpty()) {
            throw IllegalArgumentException("Cart is empty")
        }

        var totalAmount = BigDecimal.ZERO
        val orderItemsData = mutableListOf<Triple<Long, Long, Int>>()

        for (cartItem in cartItems) {
            val product = productRepository.findProductById(cartItem.productId)
                ?: throw IllegalArgumentException("Product ${cartItem.productId} not found")

            if (!product.isAvailable) {
                throw IllegalArgumentException("Product ${product.name} is not available")
            }

            val productSize = productRepository.findProductSizeById(cartItem.productSizeId)
                ?: throw IllegalArgumentException("Product size not found")

            if (productSize.stockQuantity < cartItem.quantity) {
                throw IllegalArgumentException("Not enough stock for ${product.name} (${productSize.size})")
            }

            val pricePerItem = product.basePrice + productSize.priceModifier
            val subtotal = pricePerItem * BigDecimal(cartItem.quantity)
            totalAmount += subtotal

            orderItemsData.add(Triple(cartItem.productId, cartItem.productSizeId, cartItem.quantity))
        }

        val pendingStatus = orderRepository.findOrderStatusByCode("pending")
            ?: throw IllegalArgumentException("Order status 'pending' not found")

        val order = orderRepository.createOrder(
            userId = user.id,
            addressId = address.id,
            statusId = pendingStatus.id,
            totalAmount = totalAmount
        )

        for ((productId, sizeId, quantity) in orderItemsData) {
            val product = productRepository.findProductById(productId)!!
            val productSize = productRepository.findProductSizeById(sizeId)!!
            val pricePerItem = product.basePrice + productSize.priceModifier

            orderRepository.createOrderItem(
                orderId = order.id,
                productId = productId,
                productSizeId = sizeId,
                quantity = quantity,
                priceAtPurchase = pricePerItem
            )

            orderRepository.decreaseProductStock(sizeId, quantity)
        }

        val estimatedDate = LocalDate.now().plusDays(7)
        val delivery = deliveryRepository.createDelivery(
            orderId = order.id,
            addressId = address.id,
            estimatedDeliveryDate = estimatedDate,
            status = "pending"
        )

        cartRepository.clearCart(cart.id)

        getOrderResponse(order.publicId)
    }

    suspend fun getOrder(
        userPublicId: String,
        orderPublicId: String
    ): OrderResponse {
        val user = userRepository.findByPublicId(UUID.fromString(userPublicId))
            ?: throw IllegalArgumentException("User not found")

        val order = orderRepository.findOrderByPublicId(UUID.fromString(orderPublicId))
            ?: throw IllegalArgumentException("Order not found")

        if (order.userId != user.id) {
            throw IllegalArgumentException("Access denied")
        }

        return getOrderResponse(order.publicId)
    }

    suspend fun getUserOrders(userPublicId: String): List<OrderResponse> {
        val user = userRepository.findByPublicId(UUID.fromString(userPublicId))
            ?: throw IllegalArgumentException("User not found")

        val orders = orderRepository.findOrdersByUserId(user.id)

        return orders.map { order ->
            getOrderResponse(order.publicId)
        }
    }

    private suspend fun getOrderResponse(orderPublicId: UUID): OrderResponse {
        val order = orderRepository.findOrderByPublicId(orderPublicId)
            ?: throw IllegalArgumentException("Order not found")

        val orderItems = orderRepository.findOrderItemsByOrderId(order.id)
        val orderStatus = orderRepository.findOrderStatusById(order.statusId)
            ?: throw IllegalArgumentException("Order status not found")

        val delivery = deliveryRepository.findDeliveryByOrderId(order.id)
        val address = delivery?.let { addressRepository.findAddressById(it.addressId, order.userId) }

        val items = orderItems.map { item ->
            val product = productRepository.findProductById(item.productId)
                ?: throw IllegalArgumentException("Product not found")

            val productSize = productRepository.findProductSizeById(item.productSizeId)
                ?: throw IllegalArgumentException("Product size not found")

            OrderItemResponse(
                productName = product.name,
                size = productSize.size,
                quantity = item.quantity,
                priceAtPurchase = item.priceAtPurchase.toString(),
                subtotal = item.subtotal.toString()
            )
        }

        val deliveryInfo = if (delivery != null && address != null) {
            DeliveryResponse(
                trackingNumber = delivery.trackingNumber,
                status = delivery.status,
                estimatedDate = delivery.estimatedDeliveryDate?.toString(),
                address = AddressResponse(
                    id = address.id,
                    city = address.city,
                    street = address.street,
                    house = address.house,
                    apartment = address.apartment,
                    zipCode = address.zipCode,
                    additionalInfo = address.additionalInfo,
                    isDefault = address.isDefault
                )
            )
        } else null

        return OrderResponse(
            publicId = order.publicId.toString(),
            status = orderStatus.name,
            totalAmount = order.totalAmount.toString(),
            createdAt = order.createdAt.toString(),
            items = items,
            deliveryInfo = deliveryInfo
        )
    }
}