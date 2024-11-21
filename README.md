# Spring Boot API for Vendor, Store, Product, and Category Management

This project is a Spring Boot WebFlux microservice designed to manage vendors, stores, products, and categories through RESTfull APIs and Excel file ingestion. It allows vendors to efficiently handle relationships between stores, products, and categories, supporting bulk operations via Excel files.

## Features

### API Endpoints

1. **Category Management** <code>/api/category/{vendorId}</code>
     * Create new category **POST** response status **201** 
     * Update category **PUT** <code>/{id}</code> response status **200** 
     * Find category **GET** <code>/{id}</code> response status **200** 
     * Find all categories **GET** <code>?page=0&size=25&sortField=ID&sortDirection=ASC</code> response status **200** 
     * Show categories hierarchy **GET** <code>/hierarchy</code> response status **200** 
     * Delete category **DELETE** <code>/{id}</code> response status **204** 
2. **Product Management** <code>/api/product/{vendorId}</code>
     * Create new product **POST** response status **201** 
     * Update product **PUT** <code>/{id}</code> response status **200** 
     * Find product **GET** <code>/{id}</code> response status **200** 
     * Find all products **GET** <code>?page=0&size=25&sortField=ID&sortDirection=ASC</code> response status **200** 
     * Delete product **DELETE** <code>/{id}</code> response status **204** 
3. **Store Management** <code>/api/store/{vendorId}</code>
     * Create new store **POST** response status **201** 
     * Update store **PUT** <code>/{id}</code> response status **200** 
     * Find store **GET** <code>/{id}</code> response status **200** 
     * Find all stores **GET** <code>?page=0&size=25&sortField=ID&sortDirection=ASC</code> response status **200** 
     * Delete store **DELETE** <code>/{id}</code> response status **204** 
4. **Vendor Management** <code>/api/vendor</code>
     * Create new vendor **POST** response status **201** 
     * Update vendor **PUT** <code>/{id}</code> response status **200** 
     * Find vendor **GET** <code>/{id}</code> response status **200** 
     * Find all vendors **GET** <code>?page=0&size=25&sortField=ID&sortDirection=ASC</code> response status **200** 
     * Delete vendor **DELETE** <code>/{id}</code> response status **204** 
     * Import vendors from Excel file **Post** <code>/{id}/import/excel</code> response status **200**

### Validation and Error Handling
* Validates Excel file content with detailed error messages for invalid entries.
* Robust error handling and validation for API operations.

## Technologies Used

* Spring WebFlux: Backend framework
* Spring Data Reactive Mongo: Database access
* Fastexcel: Excel file processing
* Testcontainers: MongoDB test processing
* Lombok: Simplified model creation

## Next Steps

1. Enhance authentication and authorization for API endpoints.
2. Append Client filtering
3. Implement stores search mechanism  