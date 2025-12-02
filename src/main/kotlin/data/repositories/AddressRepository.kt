package setixx.software.data.repositories

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import setixx.software.data.tables.Addresses
import setixx.software.models.Address
import setixx.software.utils.dbQuery
import java.time.Instant

class AddressRepository {

    suspend fun createAddress(
        userId: Long,
        city: String,
        street: String,
        house: String,
        apartment: String?,
        zipCode: String,
        additionalInfo: String?,
        isDefault: Boolean
    ): Address = dbQuery {
        val now = Instant.now()

        val insertStatement = Addresses.insert {
            it[Addresses.userId] = userId
            it[Addresses.city] = city
            it[Addresses.street] = street
            it[Addresses.house] = house
            it[Addresses.apartment] = apartment
            it[Addresses.zipCode] = zipCode
            it[Addresses.additionalInfo] = additionalInfo
            it[Addresses.isDefault] = isDefault
            it[Addresses.createdAt] = now
        }

        Address(
            id = insertStatement[Addresses.id],
            userId = userId,
            city = city,
            street = street,
            house = house,
            apartment = apartment,
            zipCode = zipCode,
            additionalInfo = additionalInfo,
            isDefault = isDefault,
            createdAt = now
        )
    }

    suspend fun findAddressesByUserId(userId: Long): List<Address> = dbQuery {
        Addresses.selectAll()
            .where { Addresses.userId eq userId }
            .orderBy(Addresses.isDefault to SortOrder.DESC)
            .map { rowToAddress(it) }
    }

    suspend fun findAddressById(id: Long, userId: Long): Address? = dbQuery {
        Addresses.selectAll()
            .where { (Addresses.id eq id) and (Addresses.userId eq userId) }
            .map { rowToAddress(it) }
            .singleOrNull()
    }

    suspend fun setDefaultAddress(
        id: Long,
        userId: Long
    ): Int = dbQuery {
        Addresses.update({ (Addresses.id eq id) and (Addresses.userId eq userId) }) {
            it[Addresses.isDefault] = true
        }
    }

    suspend fun deleteAddress(id: Long, userId: Long): Int = dbQuery {
        Addresses.deleteWhere { (Addresses.id eq id) and (Addresses.userId eq userId) }
    }

    private fun rowToAddress(row: ResultRow): Address {
        return Address(
            id = row[Addresses.id],
            userId = row[Addresses.userId],
            city = row[Addresses.city],
            street = row[Addresses.street],
            house = row[Addresses.house],
            apartment = row[Addresses.apartment],
            zipCode = row[Addresses.zipCode],
            additionalInfo = row[Addresses.additionalInfo],
            isDefault = row[Addresses.isDefault],
            createdAt = row[Addresses.createdAt]
        )
    }
}