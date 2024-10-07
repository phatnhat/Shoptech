package com.shoptech.admin.customer.controller;

import com.shoptech.admin.brand.BrandService;
import com.shoptech.admin.customer.CustomerService;
import com.shoptech.admin.customer.export.CustomerCsvExporter;
import com.shoptech.admin.customer.export.CustomerExcelExporter;
import com.shoptech.admin.customer.export.CustomerPdfExporter;
import com.shoptech.admin.paging.PagingAndSortingHelper;
import com.shoptech.admin.paging.PagingAndSortingParam;
import com.shoptech.common.entity.Brand;
import com.shoptech.common.entity.Country;
import com.shoptech.common.entity.Customer;
import com.shoptech.common.exception.CustomerNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/customers")
public class CustomerController {
    @Autowired
    private CustomerService customerService;

    @GetMapping
    public String listFirstPage() {
        return "redirect:/customers/page/1?sortField=createdAt&sortDir=asc";
    }

    @GetMapping("/page/{pageNum}")
    public String listByPage(@PathVariable int pageNum,
                             @PagingAndSortingParam(moduleURL = "/customers", listName = "listCustomers")
                                PagingAndSortingHelper helper){

        customerService.listByPage(pageNum, helper);

        return "customers/customers";
    }

    @GetMapping("/{id}/enabled/{status}")
    public String updateCustomerEnabledStatus(@PathVariable("id") Long id,
                                              @PathVariable("status") boolean enabled,
                                              RedirectAttributes redirectAttributes) {

        customerService.updateCustomerEnabledStatus(id, enabled);
        String status = enabled ? "enabled" : "disabled";
        String message = "The Customer ID " + id + " has been " + status;
        redirectAttributes.addFlashAttribute("message", message);

        return "redirect:/customers";
    }

    @GetMapping("/detail/{id}")
    public String viewCustomer(@PathVariable("id") Long id, Model model, RedirectAttributes ra) {
        try {
            Customer customer = customerService.get(id);
            List<Country> countries = customerService.listAllCountries();

            model.addAttribute("customer", customer);
            model.addAttribute("listCountries", countries);
            model.addAttribute("pageTitle", String.format("Edit Customer (ID: %d)", id));

            return "customers/customer_detail_modal";
        } catch (CustomerNotFoundException ex) {
            ra.addFlashAttribute("message", ex.getMessage());

            return "redirect:/customers";
        }
    }

    @GetMapping("/edit/{id}")
    public String editCustomer(@PathVariable("id") Long id, Model model, RedirectAttributes ra) {
        try {
            Customer customer = customerService.get(id);
            List<Country> countries = customerService.listAllCountries();

            model.addAttribute("listCountries", countries);
            model.addAttribute("customer", customer);
            model.addAttribute("pageTitle", String.format("Edit Customer (ID: %d)", id));

            return "customers/customer_form";
        } catch (CustomerNotFoundException ex) {
            ra.addFlashAttribute("message", ex.getMessage());
            return "redirect:/customers";
        }
    }

    @PostMapping("/save")
    public String saveCustomer(Customer customer, Model model, RedirectAttributes ra) {
        customerService.save(customer);
        ra.addFlashAttribute("message", "The customer ID " + customer.getId() + " has been updated successfully.");
        return "redirect:/customers";
    }

    @GetMapping("/delete/{id}")
    public String deleteCustomer(@PathVariable Long id, RedirectAttributes ra) {
        try {
            customerService.delete(id);
            ra.addFlashAttribute("message", "The customer ID " + id + " has been deleted successfully.");
        } catch (CustomerNotFoundException ex) {
            ra.addFlashAttribute("message", ex.getMessage());
        }
        return "redirect:/customers";
    }

    @GetMapping("/export/csv")
    public void exportToCSV(HttpServletResponse response) throws IOException {
        List<Customer> listCustomers = customerService.listAll();
        CustomerCsvExporter exporter = new CustomerCsvExporter();
        exporter.export(listCustomers, response);
    }

    @GetMapping("/export/excel")
    public void exportToExcel(HttpServletResponse response) throws IOException {
        List<Customer> listCustomers = customerService.listAll();
        CustomerExcelExporter exporter = new CustomerExcelExporter();
        exporter.export(listCustomers, response);
    }

    @GetMapping("/export/pdf")
    public void exportToPDF(HttpServletResponse response) throws IOException {
        List<Customer> listCustomers = customerService.listAll();
        CustomerPdfExporter exporter = new CustomerPdfExporter();
        exporter.export(listCustomers, response);
    }
}
