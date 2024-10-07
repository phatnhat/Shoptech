package com.shoptech.admin.shippingrate;

import com.shoptech.admin.paging.PagingAndSortingHelper;
import com.shoptech.admin.product.ProductRepository;
import com.shoptech.admin.setting.country.CountryRepository;
import com.shoptech.common.entity.Country;
import com.shoptech.common.entity.product.Product;
import com.shoptech.common.entity.ShippingRate;
import com.shoptech.common.exception.ShippingRateAlreadyExistsException;
import com.shoptech.common.exception.ShippingRateNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class ShippingRateService {
    public static final int RATES_PER_PAGE = 10;
    private static final int DIM_DIVISOR = 139;

    private ShippingRateRepository shipRepo;

    private CountryRepository countryRepo;

    private ProductRepository productRepo;

    public ShippingRateService(ShippingRateRepository shipRepo, CountryRepository countryRepo,ProductRepository productRepo) {
        super();
        this.shipRepo = shipRepo;
        this.countryRepo = countryRepo;
        this.productRepo = productRepo;
    }

    public void listByPage(int pageNum, PagingAndSortingHelper helper) {
        helper.listEntities(pageNum, RATES_PER_PAGE, shipRepo);
    }

    public List<Country> listAllCountries() {
        return countryRepo.findAllByOrderByNameAsc();
    }

    public void save(ShippingRate rateInForm) throws ShippingRateAlreadyExistsException {
        ShippingRate rateInDB = shipRepo.findByCountryAndState(
                rateInForm.getCountry().getId(), rateInForm.getState());
        boolean foundExistingRateInNewMode = rateInForm.getId() == null && rateInDB != null;
        boolean foundDifferentExistingRateInEditMode = rateInForm.getId() != null && rateInDB != null && !rateInDB.equals(rateInForm);

        if (foundExistingRateInNewMode || foundDifferentExistingRateInEditMode) {
            throw new ShippingRateAlreadyExistsException("There's already a rate for the destination "
                    + rateInForm.getCountry().getName() + ", " + rateInForm.getState());
        }
        shipRepo.save(rateInForm);
    }

    public ShippingRate get(Long id) throws ShippingRateNotFoundException {
        try {
            return shipRepo.findById(id).get();
        } catch (NoSuchElementException ex) {
            throw new ShippingRateNotFoundException("Could not find shipping rate with ID " + id);
        }
    }

    public void updateCODSupport(Long id, boolean codSupported) throws ShippingRateNotFoundException {
        Long count = shipRepo.countById(id);
        if (count == null || count == 0) {
            throw new ShippingRateNotFoundException("Could not find shipping rate with ID " + id);
        }
        ShippingRate shippingRate = shipRepo.findById(id).get();
        shippingRate.setCodSupported(codSupported);
        shipRepo.save(shippingRate);
    }

    public void delete(Long id) throws ShippingRateNotFoundException {
        Long count = shipRepo.countById(id);
        if (count == null || count == 0) {
            throw new ShippingRateNotFoundException("Could not find shipping rate with ID " + id);

        }
        shipRepo.deleteById(id);
    }

    public float calculateShippingCost(Long productId, Long countryId, String state)
            throws ShippingRateNotFoundException {
        ShippingRate shippingRate = shipRepo.findByCountryAndState(countryId, state);

        if (shippingRate == null) {
            throw new ShippingRateNotFoundException("No shipping rate found for the given "
                    + "destination. You have to enter shipping cost manually.");
        }

        Product product = productRepo.findById(productId).get();

        float dimWeight = (product.getLength() * product.getWidth() * product.getHeight()) / DIM_DIVISOR;
        float finalWeight = product.getWeight() > dimWeight ? product.getWeight() : dimWeight;

        return finalWeight * shippingRate.getRate();
    }
}
