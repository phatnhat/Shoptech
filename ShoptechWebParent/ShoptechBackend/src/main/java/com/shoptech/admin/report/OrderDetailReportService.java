package com.shoptech.admin.report;

import com.shoptech.admin.order.OrderDetailRepository;
import com.shoptech.common.entity.order.OrderDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class OrderDetailReportService extends AbstractReportService{
    @Autowired private OrderDetailRepository repo;

    @Override
    protected List<ReportItem> getReportDataByDateRangeInternal(Date startDate, Date endDate, ReportType reportType) {
        List<OrderDetail> listOrderDetails = null;

        if(reportType.equals(ReportType.CATEGORY)){
            listOrderDetails = repo.findWithCategoryAndTimeBetween(startDate, endDate);
        }else if(reportType.equals(ReportType.PRODUCT)){
            listOrderDetails = repo.findWithProductAndTimeBetween(startDate, endDate);
        }

        List<ReportItem> listReportItems = new ArrayList<>();

        for(OrderDetail details : listOrderDetails){
            String identifier = "";

            if(reportType.equals(ReportType.CATEGORY)){
                identifier = details.getProduct().getCategory().getName();
            }else if(reportType.equals(ReportType.PRODUCT)){
                identifier = details.getProduct().getShortName();
            }

            ReportItem reportItem = new ReportItem(identifier);

            float grossSales = details.getSubtotal() + details.getShippingCost();
            float netSales = details.getSubtotal() - details.getProductCost();

            int itemIndex = listReportItems.indexOf(reportItem);

            if(itemIndex >= 0){
                reportItem = listReportItems.get(itemIndex);
                reportItem.addGrossSales(grossSales);
                reportItem.addNetSales(netSales);
                reportItem.increaseProductsCount(details.getQuantity());
            }else{
                listReportItems.add(new ReportItem(identifier, grossSales, netSales, details.getQuantity()));
            }
        }
        return listReportItems;
    }
}
