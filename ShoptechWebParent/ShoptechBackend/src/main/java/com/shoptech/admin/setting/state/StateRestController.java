package com.shoptech.admin.setting.state;

import com.shoptech.admin.setting.country.CountryRepository;
import com.shoptech.common.dto.StateDTO;
import com.shoptech.common.entity.Country;
import com.shoptech.common.entity.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/states")
public class StateRestController {
    @Autowired
    private StateRepository stateRepository;

    @GetMapping("/list_by_country/{id}")
    public List<StateDTO> listByCountry(@PathVariable Long id){
        List<State> listStates = stateRepository.findByCountryOrderByNameAsc(new Country(id));
        List<StateDTO> result = new ArrayList<>();

        for(State state : listStates){
            result.add(new StateDTO(state.getId(), state.getName()));
        }

        return result;
    }

    @PostMapping("/save")
    public String save(@RequestBody State state){
        State savedState = stateRepository.save(state);
        return String.valueOf(savedState.getId());
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable Long id){
        stateRepository.deleteById(id);
    }

    @PostMapping("/check_unique")
    @ResponseBody
    public String checkUnique(@RequestBody Map<String,String> data) {

        String name = data.get("name");
        Long countryId = Long.parseLong(data.get("country_id"));
        Long stateId = Long.parseLong(data.get("state_id"));

        State countryByName = stateRepository.findByNameAndCountryId(name, countryId);
        if(countryByName == null) return "OK";

        boolean isCreatingNew = (countryByName.getId() == null);

        if (isCreatingNew) {
            if (countryByName != null) return "Duplicate";
        } else {
            if (!countryByName.getId().equals(stateId)) {
                return "Duplicate";
            }
        }

        return "OK";
    }
}
