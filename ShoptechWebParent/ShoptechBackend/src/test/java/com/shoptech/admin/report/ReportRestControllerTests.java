package com.shoptech.admin.report;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ReportRestControllerTests {
    @Autowired private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "smoke", password = "tomas", authorities = {"Salesperson"})
    public void testGetReportDateLast7Days() throws Exception {
        String requestURL = "/reports/sales_by_date/last_7_days";

        mockMvc.perform(get(requestURL))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "smoke", password = "tomas", authorities = {"Salesperson"})
    public void testGetReportDateLast6Months() throws Exception {
        String requestURL = "/reports/sales_by_date/last_6_months";

        mockMvc.perform(get(requestURL))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "smoke", password = "tomas", authorities = {"Salesperson"})
    public void testGetReportDateRange() throws Exception {
        String startDate = "2024-02-01";
        String endDate = "2024-02-10";
        String requestURL = "/reports/sales_by_date/" + startDate + "/" + endDate;

        mockMvc.perform(get(requestURL))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "smoke", password = "tomas", authorities = {"Salesperson"})
    public void testGetReportDateByCategory() throws Exception {
        String requestURL = "/reports/CATEGORY/last_7_days";

        mockMvc.perform(get(requestURL))
                .andExpect(status().isOk())
                .andDo(print());
    }
}
