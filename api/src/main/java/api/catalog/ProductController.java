package api.catalog;

import api.catalog.dto.ProductRequest;
import jakarta.validation.constraints.Min;
import org.application.catalog.dto.product.PageQuery;
import org.application.catalog.dto.product.PageResult;
import org.application.catalog.dto.product.ProductListItem;
import org.application.catalog.dto.product.ProductResult;
import org.application.catalog.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class ProductController {
    private ProductService service;
private ProductController(ProductService service){
    this.service = service;
}
@GetMapping
    public PageResult<ProductListItem> findPage(
            @RequestParam String categorySlug,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) int size
){
    return service.findPage(categorySlug, new PageQuery<>(page,size));
}

}
