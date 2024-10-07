package com.shoptech.admin.product;

import com.shoptech.admin.FileUploadUtil;
import com.shoptech.common.entity.product.Product;
import com.shoptech.common.entity.product.ProductImage;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

public class ProductSaveHelper {
    public static void deleteExtraImagesWeredRemovedOnForm(Product product) {
        String extraImageDir = "product-images/" + product.getId() + "/extras";
        Path dirPath = Paths.get(extraImageDir);

        try {
            Files.list(dirPath).forEach(file -> {
                String filename = file.toFile().getName();

                if (!product.containsImageName(filename)) {
                    try {
                        Files.delete(file);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void setExistingExtraImageNames(Product product, String[] imageIDs, String[] imageNames) {
        if(imageIDs == null || imageIDs.length ==  0) return;

        Set<ProductImage> images = new HashSet<>();
        for(int count = 0; count < imageIDs.length; count++){
            Long id = Long.parseLong(imageIDs[count]);
            String name = imageNames[count];
            images.add(new ProductImage(id, name, product));
        }

        product.setImages(images);
    }

    public static void setProductDetails(String[] detailNames, String[] detailValue, String[] detailIDs, Product product) {
        if(detailNames == null || detailNames.length == 0) return;

        for(int count = 0; count < detailNames.length; count++){
            String name = detailNames[count];
            String value = detailValue[count];
            Long id = Long.parseLong(detailIDs[count]);

            if(id != 0) product.addDetail(id, name, value);
            else if(!name.isEmpty() && !value.isEmpty()) product.addDetail(name, value);
        }
    }

    public static void saveUploadImages(Product savedProduct,
                                        MultipartFile mainImageMultipart,
                                        MultipartFile[] extraImageMultiparts) throws IOException {
        if (!mainImageMultipart.isEmpty()){
            String fileName = StringUtils.cleanPath(mainImageMultipart.getOriginalFilename());
            String uploadDir = "product-images/" + savedProduct.getId();
            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, mainImageMultipart);
        }

        if (extraImageMultiparts.length > 0) {
            String uploadDir = "product-images/" + savedProduct.getId() + "/extras";

            for (MultipartFile multipartFile : extraImageMultiparts) {
                if (multipartFile.isEmpty()) continue;

                String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
                FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
            }
        }
    }

    public static void setNewExtraImageNames(MultipartFile[] extraImageMultiparts, Product product) {
        if(extraImageMultiparts.length > 0){
            for(MultipartFile multipartFile : extraImageMultiparts){
                if(!multipartFile.isEmpty()){
                    String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
                    if(!product.containsImageName(fileName)) product.addExtraImage(fileName);
                }
            }
        }
    }

    public static void setMainImageName(MultipartFile mainImageMultipart, Product product){
        if(!mainImageMultipart.isEmpty()) {
            String fileName = StringUtils.cleanPath(mainImageMultipart.getOriginalFilename());
            product.setMainImage(fileName);
        }
    }
}
