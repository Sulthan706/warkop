package com.warkop.order.repository.order;

import com.warkop.order.model.order.OrderModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<OrderModel, Integer> {

    @Query(value = """
    SELECT *
    FROM tab_order
    WHERE status = :status
    ORDER BY created_at DESC
    """, nativeQuery = true)
    List<OrderModel> getOrderByStatus(@Param("status") String status);

    @Query(value = """
    SELECT CAST(o.created_at AS DATE)  AS tanggal,
           COUNT(*)                    AS jumlah_order,
           SUM(o.quantity)             AS total_item,
           SUM(o.quantity * o.price)   AS total_pendapatan
    FROM tab_order o
    WHERE o.status <> 'BATAL'
    GROUP BY CAST(o.created_at AS DATE)
    ORDER BY tanggal DESC
    """, nativeQuery = true)
    List<Object[]> getDailyRevenue();

    @Query(value = """
    SELECT o.product_name,
           SUM(o.quantity)           AS total_terjual,
           SUM(o.quantity * o.price) AS total_pendapatan
    FROM tab_order o
    WHERE o.status = 'SELESAI'
    GROUP BY o.product_name
    ORDER BY total_terjual DESC
    LIMIT :limit
    """, nativeQuery = true)
    List<Object[]> getTopProducts(@Param("limit") Integer limit);


    @Query(value = """
    SELECT o.customer_name,
           COUNT(*)                  AS jumlah_order,
           SUM(o.quantity * o.price) AS total_belanja
    FROM tab_order o
    WHERE o.status <> 'BATAL'
    GROUP BY o.customer_name
    HAVING SUM(o.quantity * o.price) > (
        SELECT AVG(t.total_per_customer)
        FROM (
            SELECT SUM(o2.quantity * o2.price) AS total_per_customer
            FROM tab_order o2
            WHERE o2.status <> 'BATAL'
            GROUP BY o2.customer_name
        ) t
    )
    ORDER BY total_belanja DESC
    """, nativeQuery = true)
    List<Object[]> getLoyalCustomers();
}
