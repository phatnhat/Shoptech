package com.shoptech.admin.order.controller;

import com.shoptech.admin.order.OrderService;
import com.shoptech.admin.paging.PagingAndSortingHelper;
import com.shoptech.admin.paging.PagingAndSortingParam;
import com.shoptech.admin.security.ShoptechUserDetails;
import com.shoptech.admin.setting.SettingService;
import com.shoptech.admin.utils.OrderUtil;
import com.shoptech.common.entity.Country;
import com.shoptech.common.entity.order.Order;
import com.shoptech.common.entity.order.OrderDetail;
import com.shoptech.common.entity.order.OrderStatus;
import com.shoptech.common.entity.order.OrderTrack;
import com.shoptech.common.entity.product.Product;
import com.shoptech.common.exception.OrderNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.weaver.ast.Or;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/orders")
public class OrderController {
    private String defaultRedirectURL = "redirect:/orders/page/1?sortField=createdAt&sortDir=desc";

    private OrderService orderService;

    private SettingService settingService;

    public OrderController(OrderService orderService, SettingService settingService) {
        super();
        this.orderService = orderService;
        this.settingService = settingService;
    }

    @GetMapping
    public String listFirstPage() {
        return defaultRedirectURL;
    }

    @GetMapping("/page/{pageNum}")
    public String listByPage(
            @PagingAndSortingParam(listName = "listOrders", moduleURL = "/orders") PagingAndSortingHelper helper,
            @PathVariable(name = "pageNum") int pageNum,
            @AuthenticationPrincipal ShoptechUserDetails loggedUser,
            HttpServletRequest request) {

        orderService.listByPage(pageNum, helper);
        OrderUtil.loadCurrencySetting(request, settingService);

        if (!loggedUser.hasRole("Admin") && !loggedUser.hasRole("Salesperson") && loggedUser.hasRole("Shipper")) {
            return "orders/orders_shipper";
        }

        return "orders/orders";
    }

    @GetMapping("/detail/{id}")
    public String viewOrderDetails(@PathVariable("id") Long id, Model model,
                                   RedirectAttributes ra, HttpServletRequest request,
                                   @AuthenticationPrincipal ShoptechUserDetails loggedUser
    ) {

        try {
            Order order = orderService.get(id);
            OrderUtil.loadCurrencySetting(request, settingService);

            boolean isVisibleForAdminOrSalesperson = false;
            if (loggedUser.hasRole("Admin") || loggedUser.hasRole("Salesperson")) {
                isVisibleForAdminOrSalesperson = true;
            }

            model.addAttribute("isVisibleForAdminOrSalesperson", isVisibleForAdminOrSalesperson);
            model.addAttribute("order", order);

            return "orders/order_details_modal";
        } catch (OrderNotFoundException ex) {
            ra.addFlashAttribute("messageError", ex.getMessage());
            return defaultRedirectURL;
        }

    }

    @GetMapping("/delete/{id}")
    public String deleteOrder(@PathVariable("id") Long id, Model model, RedirectAttributes ra) {

        try {
            orderService.delete(id);
            ra.addFlashAttribute("messageSuccess", "The order ID " + id + " has been deleted.");
        } catch (OrderNotFoundException ex) {
            ra.addFlashAttribute("messageError", ex.getMessage());
        }

        return defaultRedirectURL;
    }

    @GetMapping("/edit/{id}")
    public String editOrder(@PathVariable("id") Long id, Model model, RedirectAttributes ra,
                            HttpServletRequest request) {

        try {
            Order order = orderService.get(id);;

            List<Country> listCountries = orderService.listAllCountries();

            model.addAttribute("pageTitle", "Edit Order (ID: " + id + ")");
            model.addAttribute("order", order);
            model.addAttribute("listCountries", listCountries);

            return "orders/order_form";

        } catch (OrderNotFoundException ex) {
            ra.addFlashAttribute("message", ex.getMessage());
            return defaultRedirectURL;
        }

    }

    @PostMapping("/save")
    public String saveOrder(Order order, HttpServletRequest request, RedirectAttributes ra) {
        String countryName = request.getParameter("countryName");

        order.setCountry(countryName);

        updateProductDetails(order, request);
        updateOrderTracks(order, request);

        orderService.save(order);

        ra.addFlashAttribute("message", "The order ID " + order.getId() + " has been updated successfully");
        return defaultRedirectURL;
    }

    private void updateOrderTracks(Order order, HttpServletRequest request) {
        String[] trackIds = request.getParameterValues("trackId");
        String[] trackStatus = request.getParameterValues("trackStatus");
        String[] trackDates = request.getParameterValues("trackDate");
        String[] trackNotes = request.getParameterValues("trackNotes");

        List<OrderTrack> orderTracks = order.getOrderTracks();
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");

        for(int i = 0; i < trackIds.length; i++){
            OrderTrack orderTrack = new OrderTrack();
            long trackId = Long.parseLong(trackIds[i]);
            if(trackId > 0) orderTrack.setId(trackId);
            orderTrack.setOrder(order);
            orderTrack.setOrderStatus(OrderStatus.valueOf(trackStatus[i]));
            try {
                orderTrack.setUpdatedTime(dateFormatter.parse(trackDates[i]));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            orderTrack.setNotes(trackNotes[i]);

            orderTracks.add(orderTrack);
        }
    }

    private void updateProductDetails(Order order, HttpServletRequest request){
        String[] detailIds = request.getParameterValues("detailId");
        String[] productIds = request.getParameterValues("productId");
        String[] productDetailCosts = request.getParameterValues("productDetailCost");
        String[] quantities = request.getParameterValues("quantity");
        String[] productPrices = request.getParameterValues("productPrice");
        String[] productSubtotals = request.getParameterValues("productSubtotal");
        String[] productShipCosts = request.getParameterValues("productShipCost");

        Set<OrderDetail> orderDetails = order.getOrderDetails();
        for(int i = 0; i < detailIds.length; i++){
            OrderDetail orderDetail = new OrderDetail();
            long detailId = Long.parseLong(detailIds[i]);
            if(detailId > 0) orderDetail.setId(detailId);
            orderDetail.setOrder(order);
            orderDetail.setProduct(new Product(Long.parseLong(productIds[i])));
            orderDetail.setProductCost(Float.parseFloat(productDetailCosts[i]));
            orderDetail.setSubtotal(Float.parseFloat(productSubtotals[i]));
            orderDetail.setShippingCost(Float.parseFloat(productShipCosts[i]));
            orderDetail.setQuantity(Integer.parseInt(quantities[i]));
            orderDetail.setUnitPrice(Float.parseFloat(productPrices[i]));

            orderDetails.add(orderDetail);
        }
    }
}
