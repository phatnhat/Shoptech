package com.shoptech.admin.category.export;

import com.shoptech.admin.AbstractExporter;
import com.shoptech.common.entity.Category;
import jakarta.servlet.http.HttpServletResponse;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.IOException;
import java.util.List;

public class CategoryCsvExporter extends AbstractExporter {
    public void export(List<Category> listCategories, HttpServletResponse response) throws IOException {
        super.setResponseHeader(response, "text/csv", ".csv", "categories_");

        ICsvBeanWriter csvBeanWriter = new CsvBeanWriter(response.getWriter(),
                CsvPreference.STANDARD_PREFERENCE);

        String[] csvHeader = {"Category ID", "Category Name", "Alias", "Parent", "Enabled"};
        String[] fieldMapping = {"id", "name", "alias", "parent", "enabled"};

        csvBeanWriter.writeHeader(csvHeader);

        for(Category category : listCategories){
            csvBeanWriter.write(category, fieldMapping);
        }

        csvBeanWriter.close();
    }
}
