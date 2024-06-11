package edu.niu.android.instagroc.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

import edu.niu.android.instagroc.model.CartModel;
import edu.niu.android.instagroc.model.CategoryModel;
import edu.niu.android.instagroc.model.OrderModel;
import edu.niu.android.instagroc.model.ProductsModel;
import edu.niu.android.instagroc.model.ShopModel;
import edu.niu.android.instagroc.model.UsersModel;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "insra_groc_app";
    // Columns for the Users table
    private static final String TABLE_USERS = "users_table";
    private static final String KEY_ID_USER = "user_id";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_PASSWORD = "password";

    // Columns for the Shop table
    private static final String TABLE_SHOP = "users_shop";
    private static final String KEY_SHOP_ID = "shop_id";
    private static final String KEY_SHOP_NAME = "shop_name";
    private static final String KEY_SHOP_IMAGE = "shop_image";
    private static final String KEY_SHOP_LOCATION = "shop_location";

    // Columns for the Categories table
    private static final String TABLE_CATEGORIES = "users_categories";
    private static final String KEY_CATEGORIES_ID = "categories_id";
    private static final String KEY_CATEGORIES_NAME = "categories_name";
    private static final String KEY_CATEGORIES_IMAGE = "categories_image";
    // Columns for the Products table
    private static final String TABLE_PRODUCTS = "table_products";
    private static final String KEY_PRODUCTS_ID = "products_id";
    private static final String KEY_PRODUCTS_NAME = "products_name";
    private static final String KEY_PRODUCTS_PRICE = "products_price";
    private static final String KEY_PRODUCTS_OFFER_PRICE = "products_offer_price";
    private static final String KEY_PRODUCTS_QUANTITY = "products_quantity";
    private static final String KEY_PRODUCTS_DESCRIPTION = "products_description";
    private static final String KEY_PRODUCTS_IMAGE = "products_image";
    // Columns for the Cart table
    private static final String TABLE_CART = "table_cart";
    private static final String KEY_CART_ID = "cart_id";
    private static final String KEY_CART_PRODUCTS_NAME = "cart_products_name";
    private static final String KEY_CART_PRODUCTS_PRICE = "cart_products_price";
    private static final String KEY_CART_PRODUCTS_QUANTITY = "cart_products_quantity";
    private static final String KEY_CART_PRODUCTS_DESCRIPTION = "cart_products_description";
    private static final String KEY_CART_PRODUCTS_IMAGE = "cart_products_image";
    // Columns for the Orders table
    private static final String TABLE_ORDERS = "table_order";
    private static final String KEY_ORDERS_ID = "order_id";
    private static final String KEY_ORDERS_NAME = "order_name";
    private static final String KEY_ORDERS_PRICE = "order_price";
    private static final String KEY_ORDERS_TIME = "order_time";
    private static final String KEY_ORDERS_QUANTITY = "order_quantity";
    private static final String KEY_ORDERS_DESCRIPTION = "order_description";
    private static final String KEY_ORDERS_IMAGE = "order_image";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        // Create the Users table
        db.execSQL(" CREATE TABLE " + TABLE_USERS + " (" +
                KEY_ID_USER + " INTEGER PRIMARY KEY, " +
                KEY_NAME + " TEXT NOT NULL, " +
                KEY_EMAIL + " TEXT NOT NULL, " +
                KEY_PHONE + " TEXT NOT NULL, " +
                KEY_PASSWORD + " TEXT NOT NULL);"
        );

        // Create the Shop table
        db.execSQL(" CREATE TABLE " + TABLE_SHOP + " (" +
                KEY_SHOP_ID + " INTEGER PRIMARY KEY, " +
                KEY_SHOP_NAME + " TEXT NOT NULL, " +
                KEY_SHOP_LOCATION + " TEXT NOT NULL, " +
                KEY_SHOP_IMAGE + " TEXT NOT NULL);"
        );

        // Create the Categories table
        db.execSQL(" CREATE TABLE " + TABLE_CATEGORIES + " (" +
                KEY_CATEGORIES_ID + " INTEGER PRIMARY KEY, " +
                KEY_SHOP_ID + " TEXT NOT NULL, " +
                KEY_CATEGORIES_NAME + " TEXT NOT NULL, " +
                KEY_CATEGORIES_IMAGE + " TEXT NOT NULL);"
        );

        // Create the Products table
        db.execSQL(" CREATE TABLE " + TABLE_PRODUCTS + " (" +
                KEY_PRODUCTS_ID + " INTEGER PRIMARY KEY, " +
                KEY_SHOP_ID + " TEXT NOT NULL, " +
                KEY_CATEGORIES_ID + " TEXT NOT NULL, " +
                KEY_PRODUCTS_NAME + " TEXT NOT NULL, " +
                KEY_PRODUCTS_PRICE + " TEXT NOT NULL, " +
                KEY_PRODUCTS_OFFER_PRICE + " TEXT NOT NULL, " +
                KEY_PRODUCTS_QUANTITY + " TEXT NOT NULL, " +
                KEY_PRODUCTS_DESCRIPTION + " TEXT NOT NULL, " +
                KEY_PRODUCTS_IMAGE + " TEXT NOT NULL);"
        );

        // Create the Cart table
        db.execSQL(" CREATE TABLE " + TABLE_CART + " (" +
                KEY_CART_ID + " INTEGER PRIMARY KEY, " +
                KEY_ID_USER + " TEXT NOT NULL, " +
                KEY_CATEGORIES_ID + " TEXT NOT NULL, " +
                KEY_PRODUCTS_ID + " TEXT NOT NULL, " +
                KEY_CART_PRODUCTS_NAME + " TEXT NOT NULL, " +
                KEY_CART_PRODUCTS_PRICE + " TEXT NOT NULL, " +
                KEY_CART_PRODUCTS_QUANTITY + " TEXT NOT NULL, " +
                KEY_CART_PRODUCTS_DESCRIPTION + " TEXT NOT NULL, " +
                KEY_CART_PRODUCTS_IMAGE + " TEXT NOT NULL);"
        );

        // Create the Orders table
        db.execSQL(" CREATE TABLE " + TABLE_ORDERS + " (" +
                KEY_ORDERS_ID + " INTEGER PRIMARY KEY, " +
                KEY_ID_USER + " INTEGER NOT NULL, " +
                KEY_CATEGORIES_ID + " TEXT NOT NULL, " +
                KEY_PRODUCTS_ID + " TEXT NOT NULL, " +
                KEY_ORDERS_NAME + " TEXT NOT NULL, " +
                KEY_ORDERS_PRICE + " TEXT NOT NULL, " +
                KEY_ORDERS_TIME + " TEXT NOT NULL, " +
                KEY_ORDERS_QUANTITY + " TEXT NOT NULL, " +
                KEY_ORDERS_DESCRIPTION + " TEXT NOT NULL, " +
                KEY_ORDERS_IMAGE + " TEXT NOT NULL);"
        );

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop existing tables if they exist
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SHOP);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDERS);
        // Recreate the tables
        onCreate(db);
    }

    public void register(UsersModel users) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, users.getUserName());
        values.put(KEY_EMAIL, users.getEmail());
        values.put(KEY_PHONE, users.getPhone());
        values.put(KEY_PASSWORD, users.getPassword());

        db.insert(TABLE_USERS, null, values);
        db.close();
    }

    public List<UsersModel> getAllUsers() {
        List<UsersModel> usersList = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_USERS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                UsersModel users = new UsersModel();
                users.setId(Integer.parseInt(cursor.getString(0)));
                users.setUserName(cursor.getString(1));
                users.setEmail(cursor.getString(2));
                users.setPhone(cursor.getString(3));
                users.setPassword(cursor.getString(4));

                usersList.add(users);
            } while (cursor.moveToNext());
        }

        return usersList;
    }

    public void updatePassword(int userId, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_PASSWORD, newPassword);

        // Update the row where KEY_ID_USER matches the user ID
        db.update(TABLE_USERS, values, KEY_ID_USER + " = ?", new String[]{String.valueOf(userId)});

        db.close();
    }

    public void insertShop(ShopModel model) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_SHOP_NAME, model.getName());
        values.put(KEY_SHOP_LOCATION, model.getLocation());
        values.put(KEY_SHOP_IMAGE, model.getImage());

        db.insert(TABLE_SHOP, null, values);
        db.close();
    }

    public List<ShopModel> getAllShops() {
        List<ShopModel> shopList = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_SHOP;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                ShopModel model = new ShopModel();
                model.setId(Integer.parseInt(cursor.getString(0)));
                model.setName(cursor.getString(1));
                model.setLocation(cursor.getString(2));
                model.setImage(cursor.getString(3));

                shopList.add(model);
            } while (cursor.moveToNext());
        }

        return shopList;
    }
    public void insertCategory(CategoryModel model) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_SHOP_ID, model.getShopId());
        values.put(KEY_CATEGORIES_NAME, model.getName());
        values.put(KEY_CATEGORIES_IMAGE, model.getImage());

        db.insert(TABLE_CATEGORIES, null, values);
        db.close();
    }

    public List<CategoryModel> getAllCategories(int shopId) {
        List<CategoryModel> categoryList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_CATEGORIES +
                " WHERE " + KEY_SHOP_ID + " = ?";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(shopId)});

        if (cursor.moveToFirst()) {
            do {
                CategoryModel model = new CategoryModel();
                model.setId(Integer.parseInt(cursor.getString(0)));
                model.setShopId(Integer.parseInt(cursor.getString(1)));
                model.setName(cursor.getString(2));
                model.setImage(cursor.getString(3));

                categoryList.add(model);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return categoryList;
    }


    public void insertProduct(ProductsModel model) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_SHOP_ID, model.getShopId());
        values.put(KEY_CATEGORIES_ID, model.getCategoryId());
        values.put(KEY_PRODUCTS_NAME, model.getName());
        values.put(KEY_PRODUCTS_PRICE, model.getOriginalPrice());
        values.put(KEY_PRODUCTS_OFFER_PRICE, model.getOfferPrice());
        values.put(KEY_PRODUCTS_QUANTITY, model.getQuantity());
        values.put(KEY_PRODUCTS_DESCRIPTION, model.getDescription());
        values.put(KEY_PRODUCTS_IMAGE, model.getImageUri());

        db.insert(TABLE_PRODUCTS, null, values);
        db.close();
    }

    public List<ProductsModel> getProductsByCategory(int categoryId) {
        List<ProductsModel> productList = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + TABLE_PRODUCTS +
                " WHERE " + KEY_CATEGORIES_ID + " = ?";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(categoryId)});

        if (cursor.moveToFirst()) {
            do {
                ProductsModel product = new ProductsModel();
                product.setId(Integer.parseInt(cursor.getString(0)));
                product.setShopId(Integer.parseInt(cursor.getString(1)));
                product.setCategoryId(Integer.parseInt(cursor.getString(2)));
                product.setName(cursor.getString(3));
                product.setOriginalPrice(cursor.getString(4));
                product.setOfferPrice(cursor.getString(5));
                product.setQuantity(cursor.getString(6));
                product.setDescription(cursor.getString(7));
                product.setImageUri(cursor.getString(8));

                productList.add(product);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return productList;
    }

    public List<ProductsModel> getAllProductsWithOffer() {
        List<ProductsModel> productList = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + TABLE_PRODUCTS +
                " WHERE " + KEY_PRODUCTS_OFFER_PRICE + " <> ''"; // Add this condition

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                ProductsModel product = new ProductsModel();
                product.setId(Integer.parseInt(cursor.getString(0)));
                product.setShopId(Integer.parseInt(cursor.getString(1)));
                product.setCategoryId(Integer.parseInt(cursor.getString(2)));
                product.setName(cursor.getString(3));
                product.setOriginalPrice(cursor.getString(4));
                product.setOfferPrice(cursor.getString(5));
                product.setQuantity(cursor.getString(6));
                product.setDescription(cursor.getString(7));
                product.setImageUri(cursor.getString(8));

                productList.add(product);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return productList;
    }

    public ProductsModel getProductByIdAndCategory(int productId, int categoryId) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_PRODUCTS +
                " WHERE " + KEY_PRODUCTS_ID + " = ?" +
                " AND " + KEY_CATEGORIES_ID + " = ?";

        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(productId), String.valueOf(categoryId)});

        ProductsModel product = null;

        if (cursor.moveToFirst()) {
            product = new ProductsModel();
            product.setId(Integer.parseInt(cursor.getString(0)));
            product.setShopId(Integer.parseInt(cursor.getString(1)));
            product.setCategoryId(Integer.parseInt(cursor.getString(2)));
            product.setName(cursor.getString(3));
            product.setOriginalPrice(cursor.getString(4));
            product.setOfferPrice(cursor.getString(5));
            product.setQuantity(cursor.getString(6));
            product.setDescription(cursor.getString(7));
            product.setImageUri(cursor.getString(8));
        }

        cursor.close();
        db.close();

        return product;
    }


    public void deleteCategoryAndProducts(int categoryId) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Delete products associated with the category
        db.delete(TABLE_PRODUCTS, KEY_CATEGORIES_ID + " = ?", new String[]{String.valueOf(categoryId)});

        // Delete the category
        db.delete(TABLE_CATEGORIES, KEY_CATEGORIES_ID + " = ?", new String[]{String.valueOf(categoryId)});

        db.close();
    }

    public void updateProduct(ProductsModel model) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_SHOP_ID, model.getShopId());
        values.put(KEY_CATEGORIES_ID, model.getCategoryId());
        values.put(KEY_PRODUCTS_NAME, model.getName());
        values.put(KEY_PRODUCTS_PRICE, model.getOriginalPrice());
        values.put(KEY_PRODUCTS_OFFER_PRICE, model.getOfferPrice());
        values.put(KEY_PRODUCTS_QUANTITY, model.getQuantity());
        values.put(KEY_PRODUCTS_DESCRIPTION, model.getDescription());
        values.put(KEY_PRODUCTS_IMAGE, model.getImageUri());

        // Update the row where KEY_PRODUCTS_ID matches the ID of the ProductsModel
        db.update(TABLE_PRODUCTS, values, KEY_PRODUCTS_ID + " = ?", new String[]{String.valueOf(model.getId())});

        db.close();
    }

    public void deleteProduct(int productId) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Delete the row where KEY_PRODUCTS_ID matches productId
        db.delete(TABLE_PRODUCTS, KEY_PRODUCTS_ID + " = ?", new String[]{String.valueOf(productId)});

        db.close();
    }

    public void insertCart(CartModel model) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID_USER, model.getUserId());
        values.put(KEY_CATEGORIES_ID, model.getCategoryId());
        values.put(KEY_PRODUCTS_ID, model.getProductId());
        values.put(KEY_CART_PRODUCTS_NAME, model.getName());
        values.put(KEY_CART_PRODUCTS_PRICE, model.getPrice());
        values.put(KEY_CART_PRODUCTS_QUANTITY, model.getQuantity());
        values.put(KEY_CART_PRODUCTS_DESCRIPTION, model.getDescription());
        values.put(KEY_CART_PRODUCTS_IMAGE, model.getImageUri());

        db.insert(TABLE_CART, null, values);
        db.close();
    }

    public List<CartModel> getCartData(int userId) {
        List<CartModel> journalList = new ArrayList<>();

        // Use placeholders for the parameters to avoid SQL injection
        String selectQuery = "SELECT * FROM " + TABLE_CART +
                " WHERE " + KEY_ID_USER + " = ?";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            do {
                CartModel model = new CartModel();
                model.setId(Integer.parseInt(cursor.getString(0)));
                model.setUserId(Integer.parseInt(cursor.getString(1)));
                model.setCategoryId(Integer.parseInt(cursor.getString(2)));
                model.setProductId(Integer.parseInt(cursor.getString(3)));
                model.setName(cursor.getString(4));
                model.setPrice(cursor.getString(5));
                model.setQuantity(cursor.getString(6));
                model.setDescription(cursor.getString(7));
                model.setImageUri(cursor.getString(8));

                journalList.add(model);
            } while (cursor.moveToNext());
        }

        cursor.close(); // Close the cursor to avoid potential memory leaks
        return journalList;
    }

    public void updateCart(CartModel model) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID_USER, model.getUserId());
        values.put(KEY_CATEGORIES_ID, model.getCategoryId());
        values.put(KEY_PRODUCTS_ID, model.getProductId());
        values.put(KEY_CART_PRODUCTS_NAME, model.getName());
        values.put(KEY_CART_PRODUCTS_PRICE, model.getPrice());
        values.put(KEY_CART_PRODUCTS_QUANTITY, model.getQuantity());
        values.put(KEY_CART_PRODUCTS_DESCRIPTION, model.getDescription());
        values.put(KEY_CART_PRODUCTS_IMAGE, model.getImageUri());

        // Update the row where KEY_PRODUCTS_ID matches the ID of the ProductsModel
        db.update(TABLE_CART, values, KEY_CART_ID + " = ?", new String[]{String.valueOf(model.getId())});

        db.close();
    }

    public void deleteCartData(int cartId) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Delete the row where KEY_PRODUCTS_ID matches productId
        db.delete(TABLE_CART, KEY_CART_ID + " = ?", new String[]{String.valueOf(cartId)});

        db.close();
    }

    public void deleteOrder(int orderId) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Delete the row where KEY_PRODUCTS_ID matches productId
        db.delete(TABLE_ORDERS, KEY_ORDERS_ID + " = ?", new String[]{String.valueOf(orderId)});

        db.close();
    }

    public void insertOrder(OrderModel model) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID_USER, model.getUserId());
        values.put(KEY_CATEGORIES_ID, model.getCategoryId());
        values.put(KEY_PRODUCTS_ID, model.getProductId());
        values.put(KEY_ORDERS_NAME, model.getName());
        values.put(KEY_ORDERS_PRICE, model.getPrice());
        values.put(KEY_ORDERS_TIME, model.getTime());
        values.put(KEY_ORDERS_QUANTITY, model.getQuantity());
        values.put(KEY_ORDERS_DESCRIPTION, model.getDescription());
        values.put(KEY_ORDERS_IMAGE, model.getImageUri());

        db.insert(TABLE_ORDERS, null, values);
        db.close();
    }

    public List<OrderModel> getOrderData(int userId) {
        List<OrderModel> journalList = new ArrayList<>();

        // Use placeholders for the parameters to avoid SQL injection
        String selectQuery = "SELECT * FROM " + TABLE_ORDERS +
                " WHERE " + KEY_ID_USER + " = ?";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            do {
                OrderModel model = new OrderModel();
                model.setId(Integer.parseInt(cursor.getString(0)));
                model.setUserId(Integer.parseInt(cursor.getString(1)));
                model.setCategoryId(Integer.parseInt(cursor.getString(2)));
                model.setProductId(Integer.parseInt(cursor.getString(3)));
                model.setName(cursor.getString(4));
                model.setPrice(cursor.getString(5));
                model.setTime(cursor.getString(6));
                model.setQuantity(cursor.getString(7));
                model.setDescription(cursor.getString(8));
                model.setImageUri(cursor.getString(9));

                journalList.add(model);
            } while (cursor.moveToNext());
        }

        cursor.close(); // Close the cursor to avoid potential memory leaks
        return journalList;
    }

    public ShopModel getShopById(int shopId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ShopModel shop = null;

        String selectQuery = "SELECT * FROM " + TABLE_SHOP +
                " WHERE " + KEY_SHOP_ID + " = ?";

        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(shopId)});

        if (cursor.moveToFirst()) {
            shop = new ShopModel();
            shop.setId(Integer.parseInt(cursor.getString(0)));
            shop.setName(cursor.getString(1));
            shop.setLocation(cursor.getString(2));
            shop.setImage(cursor.getString(3));
        }

        cursor.close();
        db.close();

        return shop;
    }

    public void deleteShop(int shopId) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Delete products associated with the shop
        db.delete(TABLE_PRODUCTS, KEY_SHOP_ID + " = ?", new String[]{String.valueOf(shopId)});

        // Delete categories associated with the shop
        db.delete(TABLE_CATEGORIES, KEY_SHOP_ID + " = ?", new String[]{String.valueOf(shopId)});

        // Delete the shop itself
        db.delete(TABLE_SHOP, KEY_SHOP_ID + " = ?", new String[]{String.valueOf(shopId)});

        db.close();
    }



}