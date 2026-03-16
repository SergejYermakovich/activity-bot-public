package com.github.xmlreader.activitybot.controller;

import com.github.xmlreader.activitybot.dto.ActivityRequest;
import com.github.xmlreader.activitybot.dto.ActivityResponse;
import com.github.xmlreader.activitybot.dto.CategoryRequest;
import com.github.xmlreader.activitybot.dto.CategoryResponse;
import com.github.xmlreader.activitybot.service.ActivityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Activity Management", description = "API для управления активностями и категориями")
public class ActivityController {

    private final ActivityService activityService;

    // Activity endpoints
    
    @GetMapping("/activities")
    @Operation(summary = "Получить все активности", description = "Возвращает список всех активных активностей")
    public ResponseEntity<List<ActivityResponse>> getAllActivities() {
        return ResponseEntity.ok(activityService.getAllActivityResponses());
    }

    @GetMapping("/activities/category/{categoryId}")
    @Operation(summary = "Получить активности по категории", description = "Возвращает активности для указанной категории")
    public ResponseEntity<List<ActivityResponse>> getActivitiesByCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(
            activityService.getActivitiesByCategory(categoryId).stream()
                    .map(activityService::mapToActivityResponse)
                    .toList()
        );
    }

    @GetMapping("/activities/search")
    @Operation(summary = "Поиск активностей по локации", description = "Возвращает активности, содержащие указанную локацию")
    public ResponseEntity<List<ActivityResponse>> searchByLocation(@RequestParam String location) {
        return ResponseEntity.ok(
            activityService.searchActivitiesByLocation(location).stream()
                    .map(activityService::mapToActivityResponse)
                    .toList()
        );
    }

    @PostMapping("/activities")
    @Operation(summary = "Создать новую активность", description = "Создаёт новую активность с указанными параметрами")
    public ResponseEntity<ActivityResponse> createActivity(@RequestBody ActivityRequest request) {
        return ResponseEntity.ok(activityService.createActivityFromRequest(request));
    }

    // Category endpoints
    
    @GetMapping("/categories")
    @Operation(summary = "Получить все категории", description = "Возвращает список всех активных категорий")
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        return ResponseEntity.ok(activityService.getAllCategoryResponses());
    }

    @PostMapping("/categories")
    @Operation(summary = "Создать новую категорию", description = "Создаёт новую категорию с указанными параметрами")
    public ResponseEntity<CategoryResponse> createCategory(@RequestBody CategoryRequest request) {
        return ResponseEntity.ok(activityService.createCategoryFromRequest(request));
    }

    @GetMapping("/categories/init")
    @Operation(summary = "Инициализировать категории по умолчанию", description = "Создаёт стандартные категории, если их нет")
    public ResponseEntity<String> initializeCategories() {
        activityService.initializeDefaultCategories();
        return ResponseEntity.ok("Категории инициализированы");
    }
}
