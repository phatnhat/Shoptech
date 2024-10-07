package com.shoptech.admin.category;

import com.shoptech.admin.paging.PagingAndSortingHelper;
import com.shoptech.common.entity.Category;
import com.shoptech.common.exception.CategoryNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

@Service
@Transactional
public class CategoryService {
    public static final int CATEGORIES_PER_PAGE = 10;

    @Autowired
    private CategoryRepository repo;

    public List<Category> listAll(){
        return repo.findAll(Category.SORT_BY_CREATED_AT_DESC);
    }

    public void listByPage(int pageNum, PagingAndSortingHelper helper){
        helper.listEntities(pageNum, CATEGORIES_PER_PAGE, repo);
    }

    public List<Category> listCategoriesUsedInForm() {
        List<Category> categoriesUsedInForm = new ArrayList<>();
        List<Category> categoriesInDB = repo.findRootCategories();

        for (Category category : categoriesInDB) {
            if (category.getParent() == null) {
                categoriesUsedInForm.add(category);
                listChildren(categoriesUsedInForm, category, 0);
            }
        }

        return categoriesUsedInForm;
    }

    public void listChildren(List<Category> categoriesUsedInForm, Category parent, int subLevel){
        int newSubLevel = subLevel + 1;
        Set<Category> children = parent.getChildren();

        for(Category subCategory : children){
            String name = "";
            for(int i = 0; i < newSubLevel; i++){
                name += "--";
            }
            name += subCategory.getName();
            categoriesUsedInForm.add(Category.copyIdAndName(subCategory.getId(), name));
            listChildren(categoriesUsedInForm, subCategory, newSubLevel);
        }
    }

    public Category save(Category category) {
        Category parent = category.getParent();

        if(parent != null){
            String allParentIds = parent.getAllParentIDs() == null ? "-" : parent.getAllParentIDs();
            allParentIds += String.valueOf(parent.getId()) + "-";
            category.setAllParentIDs(allParentIds);
        }

        return repo.save(category);
    }

    public Category get(Long id){
        try{
            return repo.findById(id).get();
        }catch (NoSuchElementException e){
            throw new CategoryNotFoundException("Could not find any category with ID " + id);
        }
    }

    public void updateEnabled(Long id, boolean enabled){
        repo.updateEnabledStatus(id, enabled);
    }

    public String checkUnique(Long id, String name, String alias){
        boolean isCreatingNew = (id == null || id == 0);

        Category categoryByName = repo.findByName(name);

        if(isCreatingNew){
            if(categoryByName != null) return "DuplicatedName";
            else{
                Category categoryByAlias = repo.findByAlias(alias);
                if(categoryByAlias != null) return "DuplicatedAlias";
            }
        }else{
            if(categoryByName != null && categoryByName.getId() != id) return "DuplicatedName";

            Category categoryByAlias = repo.findByAlias(alias);
            if(categoryByAlias != null && categoryByAlias.getId() != id) return "DuplicatedAlias";
        }

        return "OK";
    }

    public void delete(Long id){
        Long countById = repo.countById(id);
        if(countById == null || countById == 0){
            throw new CategoryNotFoundException("Could not find any category with ID " + id);
        }

        repo.deleteById(id);
    }
}
