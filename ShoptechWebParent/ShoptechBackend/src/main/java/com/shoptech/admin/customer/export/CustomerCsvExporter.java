package com.shoptech.admin.customer.export;

import com.shoptech.admin.AbstractExporter;
import com.shoptech.common.entity.Category;
import com.shoptech.common.entity.Customer;
import jakarta.servlet.http.HttpServletResponse;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.IOException;
import java.util.List;

public class CustomerCsvExporter extends AbstractExporter {
    public void export(List<Customer> listCustomer, HttpServletResponse response) throws IOException {
        super.setResponseHeader(response, "text/csv", ".csv", "customers_");

        ICsvBeanWriter csvBeanWriter = new CsvBeanWriter(response.getWriter(),
                CsvPreference.STANDARD_PREFERENCE);

        String[] csvHeader = {"Customer ID", "First Name", "Last Name", "E-mail",  "City", "State", "Country" , "Enabled"};
        String[] fieldMapping = {"id", "firstName", "lastName", "email",  "city", "state" , "country" , "enabled"};

        csvBeanWriter.writeHeader(csvHeader);

        for (Customer customer : listCustomer) {
            csvBeanWriter.write(customer, fieldMapping);
        }

        csvBeanWriter.close();
    }
}
