package com.shoptech.admin.country;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shoptech.admin.setting.country.CountryRepository;
import com.shoptech.admin.setting.country.CountryRestController;
import com.shoptech.common.entity.Country;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.BootstrapWith;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Optional;

@SpringBootTest
@AutoConfigureMockMvc
public class CountryRestControllerTests {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    CountryRepository countryRepository;

    @Test
    @WithMockUser(username="nhatphay7@gmail.com", password="nhatphat0", roles="ADMIN")
    public void testListCountries() throws Exception {
        String url = "/countries/list";
        MvcResult result = mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        System.out.println("jsonResponse = " + jsonResponse);

        Country[] countries = objectMapper.readValue(jsonResponse, Country[].class);
        for(Country country : countries){
            System.out.println("country = " + country);
        }

        assertThat(countries).hasSizeGreaterThan(0);
    }

    @Test
    @WithMockUser(username="nhatphay7@gmail.com", password="nhatphat0", roles="ADMIN")
    public void testCreateCountries() throws Exception {
        String url = "/countries/save";
        String countryName = "Germany";
        String countryCode = "DE";
        Country country = new Country(countryName, countryCode);

        MvcResult result = mockMvc.perform(post(url)
                    .contentType("application/json")
                    .content(objectMapper.writeValueAsString(country))
                    .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        Long countryId = Long.parseLong(response);

        Optional<Country> findById = countryRepository.findById(countryId);
        assertThat(findById.isPresent());

        Country savedCountry = findById.get();
        assertThat(savedCountry.getName()).isEqualTo(countryName);
    }

    @Test
    @WithMockUser(username="nhatphay7@gmail.com", password="nhatphat0", roles="ADMIN")
    public void testUpdateCountry() throws Exception {
        String url = "/countries/save";
        Long countryId = 1L;
        String countryName = "China";
        String countryCode = "CN";
        Country country = new Country(countryId, countryName, countryCode);

        mockMvc.perform(post(url)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(country))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(String.valueOf(countryId)));

        Optional<Country> findById = countryRepository.findById(countryId);
        assertThat(findById.isPresent());

        Country savedCountry = findById.get();
        assertThat(savedCountry.getName()).isEqualTo(countryName);
    }

    @Test
    @WithMockUser(username="nhatphay7@gmail.com", password="nhatphat0", roles="ADMIN")
    public void testDeleteCountry() throws Exception {
        Long countryId = 2L;
        String url = "/countries/delete/" + countryId;

        mockMvc.perform(get(url)).andExpect(status().isOk());

        Optional<Country> findById = countryRepository.findById(countryId);
        assertThat(findById).isNotPresent();
    }
}
