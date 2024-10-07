package com.shoptech.admin;

import com.shoptech.admin.paging.PagingAndSortingArgumentResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Configuration
public class MvcConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        String dirName = "user-photos";
//        Path userPhotoDir = Paths.get(dirName);
//        String userPhotosPath = userPhotoDir.toFile().getAbsolutePath();
//        registry.addResourceHandler("/" + dirName + "/**")
//                .addResourceLocations("file:/" + userPhotosPath + "/");
//
//        String categoryImagesname = "category-images";
//        Path categoryImagesDir = Paths.get(categoryImagesname);
//        String categoryImagesPath = categoryImagesDir.toFile().getAbsolutePath();
//        registry.addResourceHandler("/" + categoryImagesname + "/**")
//                .addResourceLocations("file:/" + categoryImagesPath + "/");
//
//        String brandImagesname = "brand-images";
//        Path brandImagesDir = Paths.get(brandImagesname);
//        String brandImagesPath = brandImagesDir.toFile().getAbsolutePath();
//        registry.addResourceHandler("/" + brandImagesname + "/**")
//                .addResourceLocations("file:/" + brandImagesPath + "/");

        exposeDirectory("user-photos", registry);
        exposeDirectory("category-images", registry);
        exposeDirectory("brand-images", registry);
        exposeDirectory("product-images", registry);
        exposeDirectory("site-logo", registry);
    }

    private void exposeDirectory(String pathPattern, ResourceHandlerRegistry registry){
        Path path = Paths.get(pathPattern);
        String absolutePath = path.toFile().getAbsolutePath();

        String logicalPath = pathPattern + "/**";
        registry.addResourceHandler(logicalPath)
                .addResourceLocations("file:/" + absolutePath + "/");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new PagingAndSortingArgumentResolver());
    }
}
