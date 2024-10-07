package com.shoptech.admin.user.controller;

import com.shoptech.admin.FileUploadUtil;
import com.shoptech.admin.paging.PagingAndSortingHelper;
import com.shoptech.admin.paging.PagingAndSortingParam;
import com.shoptech.common.exception.UserNotFoundException;
import com.shoptech.admin.user.UserService;
import com.shoptech.admin.user.export.UserCsvExporter;
import com.shoptech.admin.user.export.UserExcelExporter;
import com.shoptech.admin.user.export.UserPdfExporter;
import com.shoptech.common.entity.Role;
import com.shoptech.common.entity.User;
import jakarta.servlet.http.HttpServletResponse;
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
@RequestMapping("/users")
public class UserController {
    @Autowired
    UserService userService;
    
    private String defaultRedirectURL = "redirect:/users/page/1?sortField=createdAt&sortDir=asc";

    @GetMapping
    public String findAll(){
        return defaultRedirectURL;
    }

    @GetMapping("/page/{pageNum}")
    public String listByPage(
            @PagingAndSortingParam(moduleURL = "/users", listName = "listUsers") PagingAndSortingHelper helper,
            @PathVariable int pageNum){

        userService.listByPage(pageNum, helper);

        return "users/users";
    }

    @GetMapping("/new")
    public String newUser(Model model){
        User user = new User();
        user.setEnabled(true);
        List<Role> listRoles = userService.listRoles();

        model.addAttribute("user", user);
        model.addAttribute("listRoles", listRoles);
        model.addAttribute("pageTitle", "Create New User");
        return "users/user_form";
    }

    @PostMapping("/save")
    public String saveUser(@ModelAttribute User user,
                           @RequestParam("image") MultipartFile multipartFile,
                           RedirectAttributes redirectAttributes) throws IOException {

        if(!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            user.setPhotos(fileName);
            User savedUser = userService.save(user);
            String uploadDir = "user-photos/" + savedUser.getId();
            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
        }else{
            if(user.getPhotos().isEmpty()) user.setPhotos(null);
            userService.save(user);
        }

        redirectAttributes.addFlashAttribute("message", "The user has been saved successfully.");

        String firstPartOfEmail = user.getEmail().split("@")[0];
        return "redirect:/users?sortField=id&sortDir=asc&keyword=" + firstPartOfEmail;
    }

    @GetMapping("/edit/{id}")
    public String editUser(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes){
        try{
            User user = userService.get(id);
            List<Role> listRoles = userService.listRoles();
            model.addAttribute("user", user);
            model.addAttribute("listRoles", listRoles);
            model.addAttribute("pageTitle", "Edit User (ID:" + id + ")");
            return "users/user_form";
        }catch (UserNotFoundException e){
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            return defaultRedirectURL;
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes){
        try{
            userService.delete(id);
            String userDir = "user-photos/" + id;
            FileUploadUtil.removeDir(userDir);
            redirectAttributes.addFlashAttribute("message", String.format("The user ID %d has been deleted successfully", id));
        }catch (UserNotFoundException e){
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        }
        return defaultRedirectURL;
    }

    @GetMapping("/{id}/enabled/{status}")
    public String enableUser(@PathVariable Long id, @PathVariable("status") boolean enabled, RedirectAttributes redirectAttributes){
        try{
            String status = enabled ? "enabled" : "disabled";
            String message = "The user ID " + id + " has been " + status;
            userService.updateEnabled(id, enabled);
            redirectAttributes.addFlashAttribute("message", message);
        }catch (UserNotFoundException e){
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        }
        return defaultRedirectURL;
    }

    @GetMapping("/export/csv")
    public void exportToCSV(HttpServletResponse response) throws IOException {
        List<User> listUsers = userService.listAll();
        UserCsvExporter userCsvExporter = new UserCsvExporter();
        userCsvExporter.export(listUsers, response);
    }

    @GetMapping("/export/excel")
    public void exportToExcel(HttpServletResponse response) throws IOException {
        List<User> listUsers = userService.listAll();
        UserExcelExporter userExcelExporter = new UserExcelExporter();
        userExcelExporter.export(listUsers, response);
    }

    @GetMapping("/export/pdf")
    public void exportToPDF(HttpServletResponse response) throws IOException {
        List<User> listUsers = userService.listAll();
        UserPdfExporter userPdfExporter = new UserPdfExporter();
        userPdfExporter.export(listUsers, response);
    }
}
