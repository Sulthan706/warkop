package com.warkop.order.service.order;

import com.warkop.order.dto.order.DailyRevenueDTO;
import com.warkop.order.dto.order.LoyalCustomerDTO;
import com.warkop.order.dto.order.OrderStatisticsDTO;
import com.warkop.order.dto.order.TopProductDTO;
import com.warkop.order.model.order.OrderModel;
import com.warkop.order.repository.order.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportService {

    @Autowired
    private OrderRepository orderRepository;

    public OrderStatisticsDTO getOrderStatistics() {

        List<OrderModel> orders = orderRepository.findAll();

        // groupingBy + counting: jumlah order per status
        Map<String, Long> jumlahPerStatus = orders.stream()
                .collect(Collectors.groupingBy(OrderModel::getStatus, Collectors.counting()));

        // filter + map + reduce: total pendapatan (order BATAL tidak dihitung)
        BigDecimal totalPendapatan = orders.stream()
                .filter(o -> !"BATAL".equals(o.getStatus()))
                .map(OrderModel::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // groupingBy + reducing, lalu diurutkan menurun berdasarkan value
        Map<String, BigDecimal> pendapatanPerProduk = orders.stream()
                .filter(o -> !"BATAL".equals(o.getStatus()))
                .collect(Collectors.groupingBy(
                        OrderModel::getProductName,
                        Collectors.reducing(BigDecimal.ZERO, OrderModel::getTotal, BigDecimal::add)))
                .entrySet().stream()
                .sorted(Map.Entry.<String, BigDecimal>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey, Map.Entry::getValue,
                        (a, b) -> a, LinkedHashMap::new));

        // max dengan Comparator: order dengan nilai terbesar
        OrderStatisticsDTO.BiggestOrderDTO orderTerbesar = orders.stream()
                .max(Comparator.comparing(OrderModel::getTotal))
                .map(o -> {
                    OrderStatisticsDTO.BiggestOrderDTO dto = new OrderStatisticsDTO.BiggestOrderDTO();
                    dto.setCustomerName(o.getCustomerName());
                    dto.setProductName(o.getProductName());
                    dto.setTotal(o.getTotal());
                    return dto;
                })
                .orElse(null);

        // mapToInt + average: rata-rata jumlah item per order
        double rataRataItem = orders.stream()
                .mapToInt(OrderModel::getQuantity)
                .average()
                .orElse(0);

        // map + distinct + sorted: daftar pelanggan unik
        List<String> pelangganUnik = orders.stream()
                .map(OrderModel::getCustomerName)
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        // partitioningBy: memisahkan order besar (>= Rp50.000) dan kecil
        Map<Boolean, Long> partisi = orders.stream()
                .collect(Collectors.partitioningBy(
                        o -> o.getTotal().compareTo(BigDecimal.valueOf(50000)) >= 0,
                        Collectors.counting()));

        OrderStatisticsDTO dto = new OrderStatisticsDTO();
        dto.setTotalOrder(orders.size());
        dto.setJumlahPerStatus(jumlahPerStatus);
        dto.setTotalPendapatan(totalPendapatan);
        dto.setPendapatanPerProduk(pendapatanPerProduk);
        dto.setOrderTerbesar(orderTerbesar);
        dto.setRataRataItemPerOrder(BigDecimal.valueOf(rataRataItem).setScale(2, RoundingMode.HALF_UP));
        dto.setPelangganUnik(pelangganUnik);
        dto.setJumlahOrderBesar(partisi.get(true));
        dto.setJumlahOrderKecil(partisi.get(false));

        return dto;
    }

    // [MIDDLE - NATIVE SQL] mapping Object[] hasil GROUP BY ke DTO
    public List<DailyRevenueDTO> getDailyRevenue() {

        return orderRepository.getDailyRevenue().stream().map(row -> {

            DailyRevenueDTO dto = new DailyRevenueDTO();
            dto.setTanggal(String.valueOf(row[0]));
            dto.setJumlahOrder(((Number) row[1]).longValue());
            dto.setTotalItem(((Number) row[2]).longValue());
            dto.setTotalPendapatan((BigDecimal) row[3]);

            return dto;
        }).collect(Collectors.toList());
    }

    public List<TopProductDTO> getTopProducts(Integer limit) {

        return orderRepository.getTopProducts(limit).stream().map(row -> {

            TopProductDTO dto = new TopProductDTO();
            dto.setProductName((String) row[0]);
            dto.setTotalTerjual(((Number) row[1]).longValue());
            dto.setTotalPendapatan((BigDecimal) row[2]);

            return dto;
        }).collect(Collectors.toList());
    }

    public List<LoyalCustomerDTO> getLoyalCustomers() {

        return orderRepository.getLoyalCustomers().stream().map(row -> {

            LoyalCustomerDTO dto = new LoyalCustomerDTO();
            dto.setCustomerName((String) row[0]);
            dto.setJumlahOrder(((Number) row[1]).longValue());
            dto.setTotalBelanja((BigDecimal) row[2]);

            return dto;
        }).collect(Collectors.toList());
    }
}
