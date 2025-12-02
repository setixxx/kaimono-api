package setixx.software.services

import setixx.software.data.dto.AddressResponse
import setixx.software.data.dto.CreateAddressRequest
import setixx.software.data.dto.UpdateAddressRequest
import setixx.software.data.repositories.AddressRepository
import setixx.software.data.repositories.UserRepository
import java.util.UUID

class AddressService(
    private val addressRepository: AddressRepository,
    private val userRepository: UserRepository
) {

    suspend fun createAddress(
        userPublicId: String,
        request: CreateAddressRequest
    ): AddressResponse {
        val user = userRepository.findByPublicId(UUID.fromString(userPublicId))
            ?: throw IllegalArgumentException("User not found")

        if (request.city.isBlank()) {
            throw IllegalArgumentException("City cannot be empty")
        }
        if (request.street.isBlank()) {
            throw IllegalArgumentException("Street cannot be empty")
        }
        if (request.house.isBlank()) {
            throw IllegalArgumentException("House cannot be empty")
        }
        if (request.zipCode.isBlank()) {
            throw IllegalArgumentException("Zip code cannot be empty")
        }

        val address = addressRepository.createAddress(
            userId = user.id,
            city = request.city,
            street = request.street,
            house = request.house,
            apartment = request.apartment,
            zipCode = request.zipCode,
            additionalInfo = request.additionalInfo,
            isDefault = request.isDefault
        )

        return AddressResponse(
            id = address.id,
            city = address.city,
            street = address.street,
            house = address.house,
            apartment = address.apartment,
            zipCode = address.zipCode,
            additionalInfo = address.additionalInfo,
            isDefault = address.isDefault
        )
    }

    suspend fun getAddresses(userPublicId: String): List<AddressResponse> {
        val user = userRepository.findByPublicId(UUID.fromString(userPublicId))
            ?: throw IllegalArgumentException("User not found")

        val addresses = addressRepository.findAddressesByUserId(user.id)

        return addresses.map { address ->
            AddressResponse(
                id = address.id,
                city = address.city,
                street = address.street,
                house = address.house,
                apartment = address.apartment,
                zipCode = address.zipCode,
                additionalInfo = address.additionalInfo,
                isDefault = address.isDefault
            )
        }
    }

    suspend fun setDefaultAddress(
        userPublicId: String,
        addressId: Long
    ): AddressResponse {
        val user = userRepository.findByPublicId(UUID.fromString(userPublicId))
            ?: throw IllegalArgumentException("User not found")

        val existingAddress = addressRepository.findAddressById(addressId, user.id)
            ?: throw IllegalArgumentException("Address not found")

        val updated = addressRepository.setDefaultAddress(addressId, user.id)

        if (updated == 0) {
            throw IllegalArgumentException("Failed to set default address")
        }

        val updatedAddress = addressRepository.findAddressById(addressId, user.id)
            ?: throw IllegalArgumentException("Address not found after update")

        return AddressResponse(
            id = updatedAddress.id,
            city = updatedAddress.city,
            street = updatedAddress.street,
            house = updatedAddress.house,
            apartment = updatedAddress.apartment,
            zipCode = updatedAddress.zipCode,
            additionalInfo = updatedAddress.additionalInfo,
            isDefault = updatedAddress.isDefault
        )
    }

    suspend fun deleteAddress(
        userPublicId: String,
        addressId: Long
    ) {
        val user = userRepository.findByPublicId(UUID.fromString(userPublicId))
            ?: throw IllegalArgumentException("User not found")

        val deleted = addressRepository.deleteAddress(addressId, user.id)

        if (deleted == 0) {
            throw IllegalArgumentException("Address not found or already deleted")
        }
    }
}