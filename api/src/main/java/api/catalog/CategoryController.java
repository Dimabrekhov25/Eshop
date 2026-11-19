package api.catalog;

import api.catalog.dto.CategoryRequest;
import org.application.catalog.command.CreateCategoryCommand;
import org.application.catalog.dto.CategoryResult;
import org.application.catalog.service.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController (CategoryService categoryService){
        this.categoryService = categoryService;
    }
    @GetMapping
    public List<CategoryResult> findAll(){
        return categoryService.findAll();
    }

    @PostMapping
    public ResponseEntity<CategoryResult> create(
            @RequestBody CategoryRequest categoryRequest
            )
    {
        var command = new CreateCategoryCommand(
                categoryRequest.name(),
                categoryRequest.slug(),
                categoryRequest.parentId());
        CategoryResult result = categoryService.createCategory(command);

        return ResponseEntity
                .created(URI.create("/api/categories/" + result.id()))
                .body(result);

    }
}
