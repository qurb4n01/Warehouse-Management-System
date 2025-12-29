package com.example.storeapp.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.storeapp.models.Product

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "warehouse.db"
        private const val DATABASE_VERSION = 3

        private const val TABLE_PRODUCTS = "products"
        private const val COLUMN_ID = "id"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_PRODUCT_COUNT = "productCount"
        private const val COLUMN_PRICE = "price"
        private const val COLUMN_QR_CODE = "qrCode"
        private const val COLUMN_USER_ID = "userId"

        private const val TABLE_USERS = "users"
        private const val COLUMN_USER_TABLE_ID = "id"
        private const val COLUMN_EMAIL = "email"
        private const val COLUMN_PASSWORD = "password"
        private const val COLUMN_FULL_NAME = "fullName"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createProductsTable = """
            CREATE TABLE $TABLE_PRODUCTS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NAME TEXT NOT NULL,
                $COLUMN_PRODUCT_COUNT INTEGER DEFAULT 0,
                $COLUMN_PRICE REAL DEFAULT 0.0,
                $COLUMN_QR_CODE TEXT,
                $COLUMN_USER_ID INTEGER NOT NULL
            )
        """.trimIndent()

        val createUsersTable = """
            CREATE TABLE $TABLE_USERS (
                $COLUMN_USER_TABLE_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_EMAIL TEXT UNIQUE NOT NULL,
                $COLUMN_PASSWORD TEXT NOT NULL,
                $COLUMN_FULL_NAME TEXT NOT NULL
            )
        """.trimIndent()

        db.execSQL(createUsersTable)
        db.execSQL(createProductsTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 3) {
            // Add userId column to products table
            // First add column as nullable
            db.execSQL("ALTER TABLE $TABLE_PRODUCTS ADD COLUMN $COLUMN_USER_ID INTEGER DEFAULT 0")
            // Update all existing products to have userId 0 (or you could delete them)
            // For existing installations, all products will be assigned to userId 0
            // New users will only see products they create themselves
        }
    }

    fun addProduct(product: Product): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, product.name)
            put(COLUMN_PRODUCT_COUNT, product.productCount)
            put(COLUMN_PRICE, product.price)
            put(COLUMN_QR_CODE, product.qrCode)
            put(COLUMN_USER_ID, product.userId)
        }
        return db.insert(TABLE_PRODUCTS, null, values)
    }

    fun getAllProducts(userId: Int): List<Product> {
        val products = mutableListOf<Product>()
        val db = readableDatabase
        val cursor = db.query(
            TABLE_PRODUCTS,
            null,
            "$COLUMN_USER_ID = ?",
            arrayOf(userId.toString()),
            null,
            null,
            "$COLUMN_ID DESC"
        )

        cursor.use {
            while (it.moveToNext()) {
                val product = Product(
                    id = it.getInt(it.getColumnIndexOrThrow(COLUMN_ID)),
                    name = it.getString(it.getColumnIndexOrThrow(COLUMN_NAME)),
                    productCount = it.getInt(it.getColumnIndexOrThrow(COLUMN_PRODUCT_COUNT)),
                    price = it.getDouble(it.getColumnIndexOrThrow(COLUMN_PRICE)),
                    qrCode = it.getString(it.getColumnIndexOrThrow(COLUMN_QR_CODE)),
                    userId = it.getInt(it.getColumnIndexOrThrow(COLUMN_USER_ID))
                )
                products.add(product)
            }
        }
        return products
    }

    fun deleteProduct(id: Int, userId: Int): Boolean {
        val db = writableDatabase
        val result = db.delete(TABLE_PRODUCTS, "$COLUMN_ID = ? AND $COLUMN_USER_ID = ?", arrayOf(id.toString(), userId.toString()))
        return result > 0
    }

    fun updateProduct(product: Product): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, product.name)
            put(COLUMN_PRODUCT_COUNT, product.productCount)
            put(COLUMN_PRICE, product.price)
            put(COLUMN_QR_CODE, product.qrCode)
            // userId should not change during update, but we ensure ownership
        }
        val result = db.update(TABLE_PRODUCTS, values, "$COLUMN_ID = ? AND $COLUMN_USER_ID = ?", arrayOf(product.id.toString(), product.userId.toString()))
        return result > 0
    }

    fun getProductByQrCode(qrCode: String, userId: Int): Product? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_PRODUCTS,
            null,
            "$COLUMN_QR_CODE = ? AND $COLUMN_USER_ID = ?",
            arrayOf(qrCode, userId.toString()),
            null,
            null,
            null
        )

        cursor.use {
            if (it.moveToFirst()) {
                return Product(
                    id = it.getInt(it.getColumnIndexOrThrow(COLUMN_ID)),
                    name = it.getString(it.getColumnIndexOrThrow(COLUMN_NAME)),
                    productCount = it.getInt(it.getColumnIndexOrThrow(COLUMN_PRODUCT_COUNT)),
                    price = it.getDouble(it.getColumnIndexOrThrow(COLUMN_PRICE)),
                    qrCode = it.getString(it.getColumnIndexOrThrow(COLUMN_QR_CODE)),
                    userId = it.getInt(it.getColumnIndexOrThrow(COLUMN_USER_ID))
                )
            }
        }
        return null
    }

    fun searchProductsByName(query: String, userId: Int): List<Product> {
        val products = mutableListOf<Product>()
        val db = readableDatabase
        val cursor = db.query(
            TABLE_PRODUCTS,
            null,
            "$COLUMN_NAME LIKE ? AND $COLUMN_USER_ID = ?",
            arrayOf("%$query%", userId.toString()),
            null,
            null,
            "$COLUMN_ID DESC"
        )

        cursor.use {
            while (it.moveToNext()) {
                val product = Product(
                    id = it.getInt(it.getColumnIndexOrThrow(COLUMN_ID)),
                    name = it.getString(it.getColumnIndexOrThrow(COLUMN_NAME)),
                    productCount = it.getInt(it.getColumnIndexOrThrow(COLUMN_PRODUCT_COUNT)),
                    price = it.getDouble(it.getColumnIndexOrThrow(COLUMN_PRICE)),
                    qrCode = it.getString(it.getColumnIndexOrThrow(COLUMN_QR_CODE)),
                    userId = it.getInt(it.getColumnIndexOrThrow(COLUMN_USER_ID))
                )
                products.add(product)
            }
        }
        return products
    }

    fun addUser(email: String, password: String, fullName: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_EMAIL, email)
            put(COLUMN_PASSWORD, password)
            put(COLUMN_FULL_NAME, fullName)
        }
        return db.insert(TABLE_USERS, null, values)
    }

    fun getUser(email: String, password: String): User? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_USERS,
            null,
            "$COLUMN_EMAIL = ? AND $COLUMN_PASSWORD = ?",
            arrayOf(email, password),
            null,
            null,
            null
        )

        cursor.use {
            if (it.moveToFirst()) {
                return User(
                    id = it.getInt(it.getColumnIndexOrThrow(COLUMN_USER_TABLE_ID)),
                    email = it.getString(it.getColumnIndexOrThrow(COLUMN_EMAIL)),
                    password = it.getString(it.getColumnIndexOrThrow(COLUMN_PASSWORD)),
                    fullName = it.getString(it.getColumnIndexOrThrow(COLUMN_FULL_NAME))
                )
            }
        }
        return null
    }

    fun userExists(email: String): Boolean {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_USERS,
            arrayOf(COLUMN_USER_TABLE_ID),
            "$COLUMN_EMAIL = ?",
            arrayOf(email),
            null,
            null,
            null
        )
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    fun getUserById(userId: Int): User? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_USERS,
            null,
            "$COLUMN_USER_TABLE_ID = ?",
            arrayOf(userId.toString()),
            null,
            null,
            null
        )

        cursor.use {
            if (it.moveToFirst()) {
                return User(
                    id = it.getInt(it.getColumnIndexOrThrow(COLUMN_USER_TABLE_ID)),
                    email = it.getString(it.getColumnIndexOrThrow(COLUMN_EMAIL)),
                    password = it.getString(it.getColumnIndexOrThrow(COLUMN_PASSWORD)),
                    fullName = it.getString(it.getColumnIndexOrThrow(COLUMN_FULL_NAME))
                )
            }
        }
        return null
    }

    fun updateUser(userId: Int, email: String, password: String, fullName: String): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_EMAIL, email)
            put(COLUMN_PASSWORD, password)
            put(COLUMN_FULL_NAME, fullName)
        }
        val result = db.update(TABLE_USERS, values, "$COLUMN_USER_TABLE_ID = ?", arrayOf(userId.toString()))
        return result > 0
    }

    data class User(
        val id: Int,
        val email: String,
        val password: String,
        val fullName: String
    )
}
