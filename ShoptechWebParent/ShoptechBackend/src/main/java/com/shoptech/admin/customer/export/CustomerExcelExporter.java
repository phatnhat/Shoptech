package com.shoptech.admin.customer.export;

import java.io.IOException;
import java.util.List;

import com.shoptech.admin.AbstractExporter;
import com.shoptech.common.entity.Customer;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class CustomerExcelExporter extends AbstractExporter {

    private XSSFWorkbook workbook;
    private XSSFSheet sheet;


    public CustomerExcelExporter() {
        workbook = new XSSFWorkbook();
    }

    private void writeHeaderLine() {
        sheet = workbook.createSheet("Users");
        XSSFRow row = sheet.createRow(0);

        XSSFCellStyle cellStyle = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        cellStyle.setFont(font);

        createCell(row, 0, "Customer Id", cellStyle);
        createCell(row, 1, "First Name", cellStyle);
        createCell(row, 2, "Last Name", cellStyle);
        createCell(row, 3, "E-mail", cellStyle);
        createCell(row, 4, "City", cellStyle);
        createCell(row, 5, "State", cellStyle);
        createCell(row, 6, "Country", cellStyle);
        createCell(row, 7, "Enabled", cellStyle);
    }

    private void createCell(XSSFRow row, int columnIndex, Object value, CellStyle style) {
        XSSFCell cell = row.createCell(columnIndex);
        sheet.autoSizeColumn(columnIndex);

        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else {
            cell.setCellValue((String) value);
        }

        cell.setCellStyle(style);
    }

    public void export(List<Customer> listCustomers, HttpServletResponse response) throws IOException {
        super.setResponseHeader(response, "application/octet-stream", ".xlsx", "customers_");

        writeHeaderLine();
        writeDataLines(listCustomers);

        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();


    }

    private void writeDataLines(List<Customer> listCustomers) {
        int rowIndex = 1;

        XSSFCellStyle cellStyle = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        cellStyle.setFont(font);

        for (Customer customer : listCustomers) {
            XSSFRow row = sheet.createRow(rowIndex++);
            int columnIndex = 0;

            createCell(row, columnIndex++, customer.getId(), cellStyle);
            createCell(row, columnIndex++, customer.getFirstName(), cellStyle);
            createCell(row, columnIndex++, customer.getLastName(), cellStyle);
            createCell(row, columnIndex++, customer.getEmail(), cellStyle);
            createCell(row, columnIndex++, customer.getCity(), cellStyle);
            createCell(row, columnIndex++, customer.getState(), cellStyle);
            createCell(row, columnIndex++, customer.getCountry().getName(), cellStyle);
            createCell(row, columnIndex++, customer.isEnabled(), cellStyle);
        }
    }
}