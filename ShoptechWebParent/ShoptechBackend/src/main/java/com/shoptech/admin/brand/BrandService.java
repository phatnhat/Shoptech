package com.shoptech.admin.brand;

import com.shoptech.admin.paging.PagingAndSortingHelper;
import com.shoptech.common.entity.Brand;
import com.shoptech.common.exception.BrandNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class BrandService {
    public static final int BRANDS_PER_PAGE = 5;

    @Autowired
    private BrandRepository repo;

    public List<Brand> listAll(){
        return repo.findAll();
    }

    public void listByPage(int pageNum, PagingAndSortingHelper helper){
        helper.listEntities(pageNum, BRANDS_PER_PAGE, repo);
    }

    public Brand save(Brand brand) {
        return repo.save(brand);
    }

    public Brand get(Long id){
        try{
            return repo.findById(id).get();
        }catch (NoSuchElementException e){
            throw new BrandNotFoundException("Could not find any brand with ID " + id);
        }
    }

    public void delete(Long id){
        Long countById = repo.countById(id);
        if(countById == null || countById == 0){
            throw new BrandNotFoundException("Could not find any brand with ID " + id);
        }

        repo.deleteById(id);
    }

    public String checkUnique(Long id, String name){
        boolean isCreatingNew = (id == null || id == 0);

        Brand brandByName = repo.findByName(name);

        if(isCreatingNew){
            if(brandByName != null) return "DuplicatedName";
        }else{
            if(brandByName != null && brandByName.getId() != id) return "DuplicatedName";
        }

        return "OK";
    }
}
