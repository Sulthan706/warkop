# ☕ Warkop API — Mini Project Microservices

Mini project sistem pemesanan warung kopi: **Spring Boot 3 + Java 17 + Lombok + PostgreSQL + Docker**.
Struktur package & gaya kode mengikuti pola enterprise (controller / dto / model / remote / repository / response / service, wrapper `DataResponse` + `HandlerResponse`, exception `CustomExceptionDTO`).

```
┌──────────────────┐  HTTP/REST   ┌──────────────────┐
│  order-service   │ ───────────► │ product-service  │
│     :8082        │              │      :8081       │
└────────┬─────────┘              └────────┬─────────┘
   ┌─────▼──────┐                    ┌─────▼──────┐
   │ db-order   │                    │ db-product │
   │ PostgreSQL │                    │ PostgreSQL │
   └────────────┘                    └────────────┘
```

## Struktur Package (per service)

```
com.warkop.order
├── config/          → AppConfig (@Bean), GlobalExceptionHandler
├── controller/order → REST endpoint (void + HttpServletResponse + HandlerResponse)
├── dto/exception    → CustomExceptionDTO
├── dto/order        → Request & Response DTO (Lombok @Getter @Setter)
├── model/order      → Entity JPA (Lombok, @Table "tab_order")
├── remote           → ProductRemote: HTTP call ke product-service
├── repository/order → JpaRepository + native SQL query
├── response         → DataResponse<T>, HandlerResponse
└── service/order    → Business logic (OrderService, ReportService)
```

## Cara Menjalankan

**Full Docker (demo containerization):**
```bash
docker compose up --build
```

**Development di IntelliJ (bisa debug):**
```bash
docker compose up -d db-product db-order
```
lalu Run `ProductServiceApplication` dulu, baru `OrderServiceApplication`.
Catatan IntelliJ: pastikan plugin **Lombok** aktif dan **Enable annotation processing** dicentang
(Settings → Build, Execution, Deployment → Compiler → Annotation Processors).

## Pemetaan Materi → Kode

| Materi | Lokasi |
|---|---|
| Spring IoC (@Autowired DI + component scan) | Semua Service, Controller, Remote |
| Spring IoC (@Configuration + @Bean) | `config/AppConfig.java` (bean RestTemplate & Clock) |
| Java Stream | `service/order/ReportService.getOrderStatistics()`, `ProductService.getMenuGroupedByCategory()` & `getLowStockReport()` |
| Native SQL dasar | `ProductRepository.getProductByCategory`, `searchByName`; `OrderRepository.getOrderByStatus` |
| Intermediate Native SQL | `getAboveCategoryAverage` (subquery berkorelasi), `getSummaryPerCategory` (GROUP BY), `decreaseStock` (UPDATE bersyarat), `getDailyRevenue`, `getTopProducts` (LIMIT), `getLoyalCustomers` (subquery bertingkat di HAVING) |
| Containerization | `Dockerfile` multi-stage + `docker-compose.yml` |
| Microservices | 2 service, database-per-service, komunikasi REST via `remote/ProductRemote` |

## Endpoint

Semua response dibungkus format `{ "errorCode": "00", "errorMessage": "Success", "data": ... }`.

### product-service (http://localhost:8081)
| Method | Endpoint | Keterangan |
|---|---|---|
| GET | `/api/v1/product/list` | Semua produk |
| GET | `/api/v1/product/detail?productId=1` | Detail produk |
| GET | `/api/v1/product/category?category=KOPI` | Native query filter kategori |
| GET | `/api/v1/product/search?keyword=kopi` | Native query ILIKE |
| GET | `/api/v1/product/premium` | Subquery berkorelasi |
| GET | `/api/v1/product/summary-per-category` | GROUP BY native |
| GET | `/api/v1/product/menu` | Java Stream groupingBy |
| GET | `/api/v1/product/low-stock?threshold=60` | Java Stream filter+sum |
| POST | `/api/v1/product/create` | Tambah produk (body: ProductCreateRequestDTO) |
| POST | `/api/v1/product/decrease-stock?productId=1&qty=2` | Dipanggil order-service |

### order-service (http://localhost:8082)
| Method | Endpoint | Keterangan |
|---|---|---|
| GET | `/api/v1/order/list` | Semua order |
| GET | `/api/v1/order/status?status=SELESAI` | Native query filter status |
| POST | `/api/v1/order/submit` | Buat order → memanggil product-service |
| PUT | `/api/v1/order/update-status?orderId=1&status=SELESAI` | Ubah status |
| GET | `/api/v1/report/statistics` | Java Stream (groupingBy, reduce, max, partitioningBy) |
| GET | `/api/v1/report/daily-revenue` | Native SQL GROUP BY tanggal |
| GET | `/api/v1/report/top-products?limit=3` | Native SQL GROUP BY + LIMIT |
| GET | `/api/v1/report/loyal-customers` | Native SQL subquery di HAVING |

Contoh body `/api/v1/order/submit`:
```json
{ "customerName": "Andi", "productId": 2, "quantity": 2 }
```

## Skenario Demo
1. `GET /api/v1/product/detail?productId=2` → stok 80
2. `POST /api/v1/order/submit` (productId 2, qty 2) → `{"errorCode":"00","errorMessage":"Order berhasil dibuat"}`
3. `GET /api/v1/product/detail?productId=2` → stok jadi 78 (bukti komunikasi antar service)
4. Submit qty 9999 → `{"errorCode":"01","errorMessage":"Stok produk ... tidak mencukupi"}`
5. Bandingkan `/api/v1/report/statistics` (Stream) dengan `/api/v1/report/daily-revenue` (native SQL)
