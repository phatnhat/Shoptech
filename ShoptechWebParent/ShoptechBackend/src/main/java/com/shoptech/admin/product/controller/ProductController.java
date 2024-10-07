package com.shoptech.admin.product.controller;

import com.shoptech.admin.FileUploadUtil;
import com.shoptech.admin.paging.PagingAndSortingHelper;
import com.shoptech.admin.paging.PagingAndSortingParam;
import com.shoptech.common.entity.Category;
import com.shoptech.common.exception.BrandNotFoundException;
import com.shoptech.admin.brand.BrandService;
import com.shoptech.admin.category.CategoryService;
import com.shoptech.common.exception.ProductNotFoundException;
import com.shoptech.admin.product.ProductService;
import com.shoptech.admin.security.ShoptechUserDetails;
import com.shoptech.common.exception.UserNotFoundException;
import com.shoptech.common.entity.Brand;
import com.shoptech.common.entity.product.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.shoptech.admin.product.ProductSaveHelper;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/products")
public class ProductController {
    @Autowired
    private ProductService productService;

    @Autowired
    private BrandService brandService;

    @Autowired
    private CategoryService categoryService;

    private String defaultRedirectURL = "redirect:/products/page/1?sortField=createdAt&sortDir=asc&categoryId=0";

    @GetMapping
    public String listAll(Model model){
        return defaultRedirectURL;
    }

    @GetMapping("/page/{pageNum}")
    public String listByPage(Model model,
                             @PathVariable int pageNum,
                             @PagingAndSortingParam(moduleURL = "/products", listName = "listProducts") PagingAndSortingHelper helper,
                             @RequestParam(required = false) Long categoryId){

        productService.listByPage(pageNum, helper, categoryId);
        List<Category> listCategories = categoryService.listCategoriesUsedInForm();

        if (categoryId != null) model.addAttribute("categoryId", categoryId);
        model.addAttribute("listCategories", listCategories);

        return "products/products";
    }

    @GetMapping("/new")
    public String newProduct(Model model){
        List<Brand> listBrands = brandService.listAll();

        Product product = new Product();
        product.setEnabled(true);
        product.setInStock(true);

        model.addAttribute("product", product);
        model.addAttribute("listBrands", listBrands);
        model.addAttribute("pageTitle", "Create New Product");
        model.addAttribute("numberOfExistingExtraImages", 0);

        return "products/product_form";
    }

    @PostMapping("/save")
    public String saveProduct(@ModelAttribute Product product,
                              @RequestParam(value = "fileImage", required = false) MultipartFile mainImageMultipart,
                              @RequestParam(value = "extraImage", required = false) MultipartFile[] extraImageMultiparts,
                              @RequestParam(name = "detailNames", required = false) String[] detailNames,
                              @RequestParam(name = "detailValues", required = false) String[] detailValue,
                              @RequestParam(name = "detailIDs", required = false) String[] detailIDs,
                              @RequestParam(name = "imageIDs", required = false) String[] imageIDs,
                              @RequestParam(name = "imageNames", required = false) String[] imageNames,
                              RedirectAttributes redirectAttributes,
                              @AuthenticationPrincipal ShoptechUserDetails loggedUser) throws IOException {

        if(!loggedUser.hasRole("Admin") && !loggedUser.hasRole("Editor")){
            if(loggedUser.hasRole("Salesperson")){
                productService.saveProductPrice(product);
                redirectAttributes.addFlashAttribute("message", "The product has been saved successfully.");
                return defaultRedirectURL;
            }
        }

        ProductSaveHelper.setMainImageName(mainImageMultipart, product);
        ProductSaveHelper.setExistingExtraImageNames(product, imageIDs, imageNames);
        ProductSaveHelper.setNewExtraImageNames(extraImageMultiparts, product);
        ProductSaveHelper.setProductDetails(detailNames, detailValue, detailIDs, product);

        Product savedProduct = productService.save(product);

        ProductSaveHelper.saveUploadImages(savedProduct, mainImageMultipart, extraImageMultiparts);

        ProductSaveHelper.deleteExtraImagesWeredRemovedOnForm(product);

        redirectAttributes.addFlashAttribute("message", "The product has been saved successfully.");
        return defaultRedirectURL;
    }

    @GetMapping("/{id}/enabled/{status}")
    public String enableProduct(@PathVariable Long id, @PathVariable("status") boolean enabled,
                                RedirectAttributes redirectAttributes){
        try{
            String status = enabled ? "enabled" : "disabled";
            String message = "The category ID " + id + " has been " + status;
            productService.updateEnabled(id, enabled);
            redirectAttributes.addFlashAttribute("message", message);
        }catch (UserNotFoundException e){
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        }

        return defaultRedirectURL;
    }

    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id, RedirectAttributes redirectAttributes){
        try{
            productService.delete(id);
            String productExtraImagesDir = "product-images/" + id + "/extras";
            String productImagesDir = "product-images/" + id;

            FileUploadUtil.removeDir(productExtraImagesDir);
            FileUploadUtil.removeDir(productImagesDir);

            redirectAttributes.addFlashAttribute("message", String.format("The product ID %d has been deleted successfully", id));
        }catch (ProductNotFoundException e){
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        }
        return defaultRedirectURL;
    }

    @GetMapping("/edit/{id}")
    public String editProduct(@PathVariable Long id, Model model,
                              @AuthenticationPrincipal ShoptechUserDetails loggedUser,
                              RedirectAttributes redirectAttributes){
        try{
            Product product = productService.get(id);
            List<Brand> listBrands = brandService.listAll();
            Integer numberOfExistingExtraImages = product.getImages().size();

            boolean isReadonlyForSalesperson = false;
            if(!loggedUser.hasRole("Admin") && !loggedUser.hasRole("Editor")){
                if(loggedUser.hasRole("Salesperson")){
                    isReadonlyForSalesperson = true;
                }
            }

            model.addAttribute("isReadonlyForSalesperson", isReadonlyForSalesperson);
            model.addAttribute("product", product);
            model.addAttribute("listBrands", listBrands);
            model.addAttribute("pageTitle", "Edit Product (ID:" + id + ")");
            model.addAttribute("numberOfExistingExtraImages", numberOfExistingExtraImages);
            return "products/product_form";
        }catch (BrandNotFoundException e){
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            return defaultRedirectURL;
        }
    }

    @GetMapping("/detail/{id}")
    public String viewProductDetails(@PathVariable Long id, Model model,
                                     RedirectAttributes redirectAttributes){
        try{
            Product product = productService.get(id);

            model.addAttribute("product", product);
            return "products/product_detail_modal";
        }catch (BrandNotFoundException e){
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            return defaultRedirectURL;
        }
    }
}
