package com.Telnet.pip.Controller;

import com.Telnet.pip.model.Category;
import com.Telnet.pip.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/category")
public class CategoryController {

    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping("/list")
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<Category> getCategoryList() {
        return categoryRepository.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Category> getCategoryById(@PathVariable(value = "id") Long categoryId)
            throws ResourceNotFoundException {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found ::" + categoryId));
        return ResponseEntity.ok().body(category);
    }

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Category createCategory(@Valid @RequestBody Category category) {
        return categoryRepository.save(category);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Category> updateCategory(
            @PathVariable(value = "id") Long categoryId,
            @Valid @RequestBody Category categoryDetails) throws ResourceNotFoundException {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found :: " + categoryId));
        category.setName(categoryDetails.getName());
        final Category updatedCategory = categoryRepository.save(category);
        return ResponseEntity.ok(updatedCategory);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Map<String, Boolean> deleteCategory(
            @PathVariable(value = "id") Long categoryId) throws ResourceNotFoundException {
        Category instructor = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found :: " + categoryId));

        categoryRepository.delete(instructor);
        Map<String, Boolean> response = new HashMap<>();
        response.put("Category successefully deleted", Boolean.TRUE);
        return response;
    }
}
