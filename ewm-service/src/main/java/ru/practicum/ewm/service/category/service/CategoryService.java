package ru.practicum.ewm.service.category.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.service.category.dto.CategoryDto;
import ru.practicum.ewm.service.category.model.Category;
import ru.practicum.ewm.service.category.repository.CategoryRepository;
import ru.practicum.ewm.service.event.service.EventService;
import ru.practicum.ewm.service.exceptions.CategoryHaveLinkedEventsException;
import ru.practicum.ewm.service.exceptions.CategoryNameNotUniqueException;
import ru.practicum.ewm.service.exceptions.CategoryNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final EventService eventService;
    private final ModelMapper mapper;

    public CategoryDto getCategoryById(long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException("Категория " + categoryId + " не найдена"));

        return mapper.map(category, CategoryDto.class);
    }

    public List<CategoryDto> getAllCategories(int from, int size) {
        Pageable page = PageRequest.of(from / size, size);
        return categoryRepository.findAll(page).getContent()
                .stream()
                .map(category ->  mapper.map(category, CategoryDto.class))
                .collect(Collectors.toList());
    }

    public CategoryDto create(CategoryDto category) {

        Category storageCategory;
        try {
            storageCategory = categoryRepository.save(mapper.map(category, Category.class));
        } catch (DataIntegrityViolationException e) {

            throw new CategoryNameNotUniqueException("Название категории не уникально");
        }

        return mapper.map(storageCategory, CategoryDto.class);
    }

    public CategoryDto update(CategoryDto category, long categoryId) {

        Category storageCategory;

        getCategoryById(categoryId);
        category.setId(categoryId);

        try {
            storageCategory = categoryRepository.save(mapper.map(category, Category.class));
        } catch (DataIntegrityViolationException e) {
            throw new CategoryNameNotUniqueException("Название категории не уникально");
        }

        return mapper.map(storageCategory, CategoryDto.class);
    }

    public void delete(long categoryId) {
        CategoryDto category = getCategoryById(categoryId);
        if (eventService.getEventsCountByCategoryId(categoryId) > 0) {
            throw new CategoryHaveLinkedEventsException("С категорией " + categoryId + " связаны события");
        } else {
            categoryRepository.delete(mapper.map(category, Category.class));
        }
    }
}
