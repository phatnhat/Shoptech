package com.shoptech.admin.brand.controller;

import com.shoptech.common.exception.BrandNotFoundException;
import com.shoptech.admin.brand.BrandService;
import com.shoptech.admin.brand.CategoryDTO;
import com.shoptech.common.entity.Brand;
import com.shoptech.common.entity.Category;
import com.shoptech.common.exception.BrandNotFoundRestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/brands")
public class BrandRestController {
    @Autowired
    private BrandService brandService;

    @PostMapping("/check_unique")
    public String checkUnique(@Param("id") Long id, @Param("name") String name){
        return brandService.checkUnique(id, name);
    }

    @GetMapping("/{id}/categories")
    public List<CategoryDTO> listCategoriesByBrand(@PathVariable("id") Long brandId) throws BrandNotFoundRestException {
        List<CategoryDTO> listCategories = new ArrayList<>();

        try{
            Brand brand = brandService.get(brandId);
            Set<Category> categories = brand.getCategories();

            for(Category category : categories){
                CategoryDTO categoryDTO = new CategoryDTO(category.getId(), category.getName());
                listCategories.add(categoryDTO);
            }

            return listCategories;
        }catch (BrandNotFoundException e){
            throw new BrandNotFoundRestException();
        }
    }
}
