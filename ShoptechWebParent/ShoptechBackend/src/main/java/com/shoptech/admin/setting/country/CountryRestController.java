package com.shoptech.admin.setting.country;

import com.shoptech.common.entity.Country;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/countries")
public class CountryRestController {
    @Autowired
    private CountryRepository countryRepository;

    @GetMapping("/list")
    public List<Country> listAll(){
        return countryRepository.findAllByOrderByNameAsc();
    }

    @PostMapping("/save")
    public String save(@RequestBody Country country){
        Country savedCountry = countryRepository.save(country);
        return String.valueOf(savedCountry.getId());
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable Long id){
        countryRepository.deleteById(id);
    }

    @PostMapping("/check_unique")
    @ResponseBody
    public String checkUnique(@RequestBody Map<String,String> data) {

        String name = data.get("name");
        Long id = Long.parseLong(data.get("id"));
        System.out.println("id = " + id);

        Country countryByName = countryRepository.findByName(name);

        if(countryByName == null) {
            return "OK";
        }

        boolean isCreatingNew = (countryByName.getId() == null);

        if (isCreatingNew) {
            if (countryByName != null) {
                return "Duplicate";
            }
        } else {
            if (!countryByName.getId().equals(id)) {
                return "Duplicate";
            }
        }
        return "OK";
    }
}
