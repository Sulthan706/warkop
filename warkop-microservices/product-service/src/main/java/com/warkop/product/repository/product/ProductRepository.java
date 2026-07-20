package com.warkop.product.repository.product;

import com.warkop.product.model.product.ProductModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<ProductModel, Integer> {


    @Query(value = """
    SELECT *
    FROM tab_product
    WHERE category = :category
    ORDER BY price ASC
    """, nativeQuery = true)
    List<ProductModel> getProductByCategory(@Param("category") String category);


    @Query(value = """
    SELECT *
    FROM tab_product
    WHERE product_name ILIKE CONCAT('%', :keyword, '%')
    """, nativeQuery = true)
    List<ProductModel> searchByName(@Param("keyword") String keyword);

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

    @Modifying
    @Query(value = """
    UPDATE tab_product
    SET stock = stock - :qty
    WHERE product_id = :productId
    AND stock >= :qty
    """, nativeQuery = true)
    int decreaseStock(@Param("productId") Integer productId, @Param("qty") Integer qty);
}
