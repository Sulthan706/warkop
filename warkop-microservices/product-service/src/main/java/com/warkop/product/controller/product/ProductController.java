package com.warkop.product.controller.product;

import com.warkop.product.dto.exception.CustomExceptionDTO;
import com.warkop.product.dto.product.LowStockReportDTO;
import com.warkop.product.dto.product.ProductCreateRequestDTO;
import com.warkop.product.dto.product.ProductSummaryDTO;
import com.warkop.product.model.product.ProductModel;
import com.warkop.product.response.DataResponse;
import com.warkop.product.response.HandlerResponse;
import com.warkop.product.service.product.ProductService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/v1/product", produces = {"application/json"})
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/list")
    public void getAllProduct(HttpServletResponse response) throws IOException {

        DataResponse<List<ProductModel>> data = new DataResponse<>();
        data.setData(productService.getAllProduct());
        HandlerResponse.responseSuccessWithData(response, data);
    }

    @GetMapping("/detail")
    public void getProductDetail(HttpServletResponse response, @RequestParam Integer productId) throws IOException, CustomExceptionDTO {

        DataResponse<ProductModel> data = new DataResponse<>();
        data.setData(productService.getProductById(productId));
        HandlerResponse.responseSuccessWithData(response, data);
    }

    @GetMapping("/category")
    public void getProductByCategory(HttpServletResponse response, @RequestParam String category) throws IOException {

        DataResponse<List<ProductModel>> data = new DataResponse<>();
        data.setData(productService.getProductByCategory(category));
        HandlerResponse.responseSuccessWithData(response, data);
    }

    @GetMapping("/search")
    public void searchProduct(HttpServletResponse response, @RequestParam String keyword) throws IOException {

        DataResponse<List<ProductModel>> data = new DataResponse<>();
        data.setData(productService.searchProduct(keyword));
        HandlerResponse.responseSuccessWithData(response, data);
    }

    @GetMapping("/premium")
    public void getPremiumProduct(HttpServletResponse response) throws IOException {

        DataResponse<List<ProductModel>> data = new DataResponse<>();
        data.setData(productService.getPremiumProduct());
        HandlerResponse.responseSuccessWithData(response, data);
    }

    @GetMapping("/summary-per-category")
    public void getSummaryPerCategory(HttpServletResponse response) throws IOException {

        DataResponse<List<ProductSummaryDTO>> data = new DataResponse<>();
        data.setData(productService.getSummaryPerCategory());
        HandlerResponse.responseSuccessWithData(response, data);
    }

    @GetMapping("/menu")
    public void getMenu(HttpServletResponse response) throws IOException {

        DataResponse<Map<String, List<String>>> data = new DataResponse<>();
        data.setData(productService.getMenuGroupedByCategory());
        HandlerResponse.responseSuccessWithData(response, data);
    }

    @GetMapping("/low-stock")
    public void getLowStock(HttpServletResponse response, @RequestParam(defaultValue = "60") Integer threshold) throws IOException {

        DataResponse<LowStockReportDTO> data = new DataResponse<>();
        data.setData(productService.getLowStockReport(threshold));
        HandlerResponse.responseSuccessWithData(response, data);
    }

    @PostMapping("/create")
    public void createProduct(HttpServletResponse response, @RequestBody ProductCreateRequestDTO request) throws IOException, CustomExceptionDTO {

        productService.createProduct(request);
        HandlerResponse.responseSuccessOK(response, "Insert Produk Berhasil");
    }

    @PostMapping("/decrease-stock")
    public void decreaseStock(HttpServletResponse response,
                              @RequestParam Integer productId,
                              @RequestParam Integer qty) throws IOException {

        boolean success = productService.decreaseStock(productId, qty);

        if (!success) {
            HandlerResponse.responseBadRequest(response, "01", "Stok tidak mencukupi atau produk tidak ditemukan");
            return;
        }

        HandlerResponse.responseSuccessOK(response, "Stok berhasil dikurangi");
    }
}
