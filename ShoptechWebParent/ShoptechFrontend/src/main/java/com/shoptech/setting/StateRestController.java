package com.shoptech.setting;

import com.shoptech.common.dto.StateDTO;
import com.shoptech.common.entity.Country;
import com.shoptech.common.entity.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/settings")
public class StateRestController {
    @Autowired
    private StateRepository stateRepository;

    @GetMapping("/list_states_by_country/{id}")
    public List<StateDTO> listByCountry(@PathVariable Long id){
        List<State> listStates = stateRepository.findByCountryOrderByNameAsc(new Country(id));
        List<StateDTO> result = new ArrayList<>();

        for(State state : listStates){
            result.add(new StateDTO(state.getId(), state.getName()));
        }

        return result;
    }
}
