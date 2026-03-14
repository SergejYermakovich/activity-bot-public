package com.github.xmlreader.activitybot.service;

import com.github.xmlreader.activitybot.dto.ActivityRequest;
import com.github.xmlreader.activitybot.dto.ActivityResponse;
import com.github.xmlreader.activitybot.dto.CategoryRequest;
import com.github.xmlreader.activitybot.dto.CategoryResponse;
import com.github.xmlreader.activitybot.entity.Activity;
import com.github.xmlreader.activitybot.entity.Category;
import com.github.xmlreader.activitybot.exception.NotFoundException;
import com.github.xmlreader.activitybot.exception.ValidationException;
import com.github.xmlreader.activitybot.repository.ActivityRepository;
import com.github.xmlreader.activitybot.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ActivityService {
    
    private final ActivityRepository activityRepository;
    private final CategoryRepository categoryRepository;
    
    public List<Activity> getAllActiveActivities() {
        return activityRepository.findByIsActiveTrue();
    }
    
    public List<Activity> getActivitiesByCategory(Long categoryId) {
        return activityRepository.findByCategoryIdAndIsActiveTrue(categoryId);
    }
    
    public List<Activity> searchActivitiesByLocation(String location) {
        return activityRepository.findByLocationContainingIgnoreCaseAndIsActiveTrue(location);
    }
    
    public List<Category> getAllActiveCategories() {
        return categoryRepository.findByIsActiveTrue();
    }
    
    @Transactional
    public Activity createActivity(Activity activity) {
        log.info("Creating new activity: {}", activity.getTitle());
        return activityRepository.save(activity);
    }
    
    @Transactional
    public Category createCategory(Category category) {
        log.info("Creating new category: {}", category.getName());
        return categoryRepository.save(category);
    }
    
    @Transactional
    public void initializeDefaultCategories() {
        if (categoryRepository.count() == 0) {
            log.info("Initializing default categories...");
            
            List<Category> defaultCategories = List.of(
                Category.builder().name("Квесты").emoji("🧩").description("Комнаты-квесты, escape rooms").build(),
                Category.builder().name("Футбол").emoji("⚽").description("Футбольные матчи, аренда полей").build(),
                Category.builder().name("Ивенты").emoji("🎪").description("Концерты, выставки, фестивали").build(),
                Category.builder().name("Спорт").emoji("🏃").description("Спортивные мероприятия").build(),
                Category.builder().name("Воркшопы").emoji("🔧").description("Мастер-классы, обучение").build(),
                Category.builder().name("Игры").emoji("🎮").description("Настольные игры, киберспорт").build()
            );
            
            categoryRepository.saveAll(defaultCategories);
            log.info("Default categories initialized");
        }
    }
    
    // New methods for REST API
    
    @Transactional
    public ActivityResponse createActivityFromRequest(ActivityRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new NotFoundException("Категория", request.getCategoryId()));
        
        validateActivityRequest(request);
        
        Activity activity = Activity.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .category(category)
                .location(request.getLocation())
                .price(request.getPrice())
                .minParticipants(request.getMinParticipants())
                .maxParticipants(request.getMaxParticipants())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .isActive(true)
                .build();
        
        Activity savedActivity = activityRepository.save(activity);
        return mapToActivityResponse(savedActivity);
    }
    
    public List<ActivityResponse> getAllActivityResponses() {
        return activityRepository.findByIsActiveTrue().stream()
                .map(this::mapToActivityResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public CategoryResponse createCategoryFromRequest(CategoryRequest request) {
        if (categoryRepository.findByName(request.getName()) != null) {
            throw new ValidationException("Категория с таким названием уже существует");
        }
        
        Category category = Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .emoji(request.getEmoji())
                .isActive(true)
                .build();
        
        Category savedCategory = categoryRepository.save(category);
        return mapToCategoryResponse(savedCategory);
    }
    
    public List<CategoryResponse> getAllCategoryResponses() {
        return categoryRepository.findByIsActiveTrue().stream()
                .map(this::mapToCategoryResponse)
                .collect(Collectors.toList());
    }
    
    private ActivityResponse mapToActivityResponse(Activity activity) {
        Category category = activity.getCategory();
        
        return ActivityResponse.builder()
                .id(activity.getId())
                .title(activity.getTitle())
                .description(activity.getDescription())
                .categoryName(category != null ? category.getName() : null)
                .categoryEmoji(category != null ? category.getEmoji() : null)
                .location(activity.getLocation())
                .price(activity.getPrice())
                .minParticipants(activity.getMinParticipants())
                .maxParticipants(activity.getMaxParticipants())
                .startTime(activity.getStartTime())
                .endTime(activity.getEndTime())
                .isActive(activity.getIsActive())
                .createdAt(activity.getCreatedAt())
                .updatedAt(activity.getUpdatedAt())
                .build();
    }
    
    private CategoryResponse mapToCategoryResponse(Category category) {
        int activityCount = category.getActivities() != null ? category.getActivities().size() : 0;
        
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .emoji(category.getEmoji())
                .isActive(category.getIsActive())
                .createdAt(category.getCreatedAt())
                .activityCount(activityCount)
                .build();
    }
    
    private void validateActivityRequest(ActivityRequest request) {
        if (request.getStartTime() != null && request.getEndTime() != null) {
            if (request.getEndTime().isBefore(request.getStartTime())) {
                throw new ValidationException("Время окончания не может быть раньше времени начала");
            }
        }
        
        if (request.getMinParticipants() != null && request.getMaxParticipants() != null) {
            if (request.getMinParticipants() > request.getMaxParticipants()) {
                throw new ValidationException("Минимальное количество участников не может быть больше максимального");
            }
        }
        
        if (request.getPrice() != null && request.getPrice().compareTo(java.math.BigDecimal.ZERO) < 0) {
            throw new ValidationException("Цена не может быть отрицательной");
        }
    }
}
