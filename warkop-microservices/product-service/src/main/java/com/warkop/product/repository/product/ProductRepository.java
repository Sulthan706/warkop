package com.warkop.product.repository.product;

import com.warkop.product.model.product.ProductModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<ProductModel, Integer> {

    // [JUNIOR - NATIVE SQL] filter kategori
    @Query(value = """
    SELECT *
    FROM tab_product
    WHERE category = :category
    ORDER BY price ASC
    """, nativeQuery = true)
    List<ProductModel> getProductByCategory(@Param("category") String category);

    // [JUNIOR - NATIVE SQL] pencarian nama, case-insensitive (ILIKE khusus PostgreSQL)
    @Query(value = """
    SELECT *
    FROM tab_product
    WHERE product_name ILIKE CONCAT('%', :keyword, '%')
    """, nativeQuery = true)
    List<ProductModel> searchByName(@Param("keyword") String keyword);

    // [MIDDLE - NATIVE SQL] subquery berkorelasi:
    // produk yang harganya di atas rata-rata harga kategorinya sendiri
    @Query(value = """
    SELECT p.*
    FROM tab_product p
    WHERE p.price > (
        SELECT AVG(p2.price)
        FROM tab_product p2
        WHERE p2.category = p.category
    )
    ORDER BY p.category, p.price DESC
    """, nativeQuery = true)
    List<ProductModel> getAboveCategoryAverage();

    // [MIDDLE - NATIVE SQL] agregasi + GROUP BY per kategori
    @Query(value = """
    SELECT p.category               AS category,
           COUNT(*)                 AS total_produk,
           MIN(p.price)             AS harga_termurah,
           MAX(p.price)             AS harga_termahal,
           ROUND(AVG(p.price), 2)   AS harga_rata_rata,
           SUM(p.stock)             AS total_stok
    FROM tab_product p
    GROUP BY p.category
    ORDER BY total_produk DESC
    """, nativeQuery = true)
    List<Object[]> getSummaryPerCategory();

    // [MIDDLE - NATIVE SQL] UPDATE bersyarat, atomik di database.
    // Return 0 = stok tidak cukup / produk tidak ada.
    @Modifying
    @Query(value = """
    UPDATE tab_product
    SET stock = stock - :qty
    WHERE product_id = :productId
    AND stock >= :qty
    """, nativeQuery = true)
    int decreaseStock(@Param("productId") Integer productId, @Param("qty") Integer qty);
}
