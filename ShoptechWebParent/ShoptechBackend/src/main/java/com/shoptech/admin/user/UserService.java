package com.shoptech.admin.user;

import com.shoptech.admin.paging.PagingAndSortingHelper;
import com.shoptech.common.entity.Role;
import com.shoptech.common.entity.User;
import com.shoptech.common.exception.UserNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class UserService {
    public static final int USERS_PER_PAGE = 2;

    @Autowired
    private UserRepository repo;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<User> listAll(){
        return repo.findAll();
    }

    public User getByEmail(String email){
        return repo.getUserByEmail(email);
    }

    public void listByPage(int pageNum, PagingAndSortingHelper helper){
        helper.listEntities(pageNum, USERS_PER_PAGE, repo);
    }

    public List<Role> listRoles(){
        return roleRepository.findAll();
    }

    public User save(User user){
        boolean isUpdatingUser = (user.getId() != null);
        if(isUpdatingUser){
            User existingUser = repo.findById(user.getId()).get();
            if(user.getPassword().isEmpty()) user.setPassword(existingUser.getPassword());
            else encodePassword(user);
        }else{
            encodePassword(user);
        }
        return repo.save(user);
    }

    public User updateAccount(User userInForm){
        User userInDB = repo.findById(userInForm.getId()).get();

        if(!userInForm.getPassword().isEmpty()){
            userInDB.setPassword(userInForm.getPassword());
            encodePassword(userInDB);
        }

        if(userInForm.getPhotos() != null){
            userInDB.setPhotos(userInForm.getPhotos());
        }

        userInDB.setFirstName(userInForm.getFirstName());
        userInDB.setLastName(userInForm.getLastName());

        return repo.save(userInDB);
    }

    public void encodePassword(User user){
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
    }

    public boolean isEmailUnique(Long id, String email){
        User user = repo.getUserByEmail(email);

        if(user == null) return true;

        boolean isCreatingNew = (id == null);

        if(isCreatingNew){
            if(user != null) return false;
        }else{
            if(!user.getId().equals(id)) return false;
        }

        return true;
    }

    public User get(Long id){
        try{
            return repo.findById(id).get();
        }catch (NoSuchElementException e){
            throw new UserNotFoundException("Could not find any user with ID " + id);
        }
    }

    public void delete(Long id){
        Long countById = repo.countById(id);
        if(countById == null || countById == 0){
            throw new UserNotFoundException("Could not find any user with ID " + id);
        }

        repo.deleteById(id);
    }

    public void updateEnabled(Long id, boolean enabled){
        repo.updateEnabledStatus(id, enabled);
    }
}
