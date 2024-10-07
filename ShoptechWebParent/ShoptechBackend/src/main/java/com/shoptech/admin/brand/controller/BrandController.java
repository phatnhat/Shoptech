package com.shoptech.admin.brand.controller;

import com.shoptech.admin.FileUploadUtil;
import com.shoptech.admin.paging.PagingAndSortingHelper;
import com.shoptech.admin.paging.PagingAndSortingParam;
import com.shoptech.common.exception.BrandNotFoundException;
import com.shoptech.admin.brand.BrandService;
import com.shoptech.admin.category.CategoryService;
import com.shoptech.common.entity.Brand;
import com.shoptech.common.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/brands")
public class BrandController {
    @Autowired
    private BrandService brandService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public String listAll(Model model){
        return "redirect:/brands/page/1?sortField=createdAt&sortDir=asc";
    }

    @GetMapping("/page/{pageNum}")
    public String listByPage(Model model,
                             @PathVariable int pageNum,
                             @PagingAndSortingParam(moduleURL = "/brands", listName = "listBrands")PagingAndSortingHelper helper){

        brandService.listByPage(pageNum, helper);

        return "brands/brands";
    }

    @GetMapping("/new")
    public String newBrand(Model model){
        List<Category> listCategories = categoryService.listCategoriesUsedInForm();

        model.addAttribute("brand", new Brand());
        model.addAttribute("listCategories", listCategories);
        model.addAttribute("pageTitle", "Create New Brand");
        return "brands/brand_form";
    }

    @GetMapping("/edit/{id}")
    public String editCategory(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes){
        try{
            Brand brand = brandService.get(id);
            List<Category> listCategories = categoryService.listCategoriesUsedInForm();

            model.addAttribute("brand", brand);
            model.addAttribute("listCategories", listCategories);
            model.addAttribute("pageTitle", "Edit Brand (ID:" + id + ")");
            return "brands/brand_form";
        }catch (BrandNotFoundException e){
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            return "redirect:/brands";
        }
    }

    @PostMapping("/save")
    public String saveBrand(@ModelAttribute Brand brand, @RequestParam("fileImage") MultipartFile multipartFile,
                            RedirectAttributes redirectAttributes) throws IOException {
        if(!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            brand.setLogo(fileName);
            Brand savedBrand = brandService.save(brand);
            String uploadDir = "brand-images/" + savedBrand.getId();
            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
        }else{
            if(brand.getLogo().isEmpty()) brand.setLogo(null);
            brandService.save(brand);
        }

        redirectAttributes.addFlashAttribute("message", "The brand has been saved successfully.");
        return "redirect:/brands";
    }

    @GetMapping("/delete/{id}")
    public String deleteBrand(@PathVariable Long id, RedirectAttributes redirectAttributes){
        try{
            brandService.delete(id);
            String brandDir = "brand-images/" + id;
            FileUploadUtil.removeDir(brandDir);
            redirectAttributes.addFlashAttribute("message", String.format("The brand ID %d has been deleted successfully", id));
        }catch (BrandNotFoundException e){
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        }
        return "redirect:/brands";
    }
}
