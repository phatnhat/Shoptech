package com.shoptech.admin.user;

import com.shoptech.common.entity.Role;
import com.shoptech.common.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class UserRepositoryTests {
    @Autowired
    UserRepository userRepository;

    @Autowired
    TestEntityManager testEntityManager;

    @Test
    public void testCreateUser(){
        Role roleAdmin = testEntityManager.find(Role.class, 1);
        User userTomas = new User("nhatphay@gmail.com", "tomas123", "Tomas", "Vrbada");
        userTomas.addRole(roleAdmin);

        User savedUser = userRepository.save(userTomas);
        assertThat(savedUser.getId()).isGreaterThan(0);
    }

    @Test
    public void testCreateUserWithTwoRoles(){
        Role roleEditor = new Role(3l);
        Role roleShipper = new Role(5l);
        User userBihan = new User("abc123@gmail.com", "bihan123", "Bi", "Han");
        userBihan.addRole(roleEditor);
        userBihan.addRole(roleShipper);

        User savedUser = userRepository.save(userBihan);
        assertThat(savedUser.getId()).isGreaterThan(0);
    }

    @Test
    public void testListAllUsers(){
        Iterable<User> users = userRepository.findAll();
        users.forEach(System.out::println);
    }

    @Test
    public void testGetUserById(){
        User user = userRepository.findById(1L).get();
        System.out.println(user);
        assertThat(user).isNotNull();
    }

    @Test
    public void testUpdateUserDetails(){
        User user = userRepository.findById(1L).get();
        user.setEnabled(true);
        user.setEmail("nhatphay7@gmail.com");
        userRepository.save(user);
    }

    @Test
    public void testUpdateUserRoles(){
        User user = userRepository.findById(2L).get();
        Role roleEditor = new Role(3L);
        Role roleSalesperson = new Role(3L);
        user.getRoles().remove(roleEditor);
        user.addRole(roleSalesperson);
        userRepository.save(user);
    }

    @Test
    public void testDeleteUser(){
        Long userId = 2L;
        userRepository.deleteById(userId);
    }

    @Test
    public void testGetUserByEmail(){
        String email = "nhatphay7@gmail.com";
        User user = userRepository.getUserByEmail(email);
        assertThat(user).isNotNull();
    }

    @Test
    public void testCountById(){
        Long id = 1L;
        Long countById = userRepository.countById(id);
        assertThat(countById).isNotNull().isGreaterThan(0);
    }

    @Test
    public void testDisableUser(){
        Long id = 1L;
        userRepository.updateEnabledStatus(id, false);
    }

    @Test
    public void testEnableUser(){
        Long id = 1L;
        userRepository.updateEnabledStatus(id, true);
    }

    @Test
    public void testListFirstPage(){
        int pageNumber = 0;
        int pageSize = 2;

        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<User> page = userRepository.findAll(pageable);
        List<User> listUsers = page.getContent();
        listUsers.forEach(System.out::println);

        assertThat(listUsers.size()).isEqualTo(pageSize);
    }

    @Test
    public void testSearchUser(){
        String keyword = "tomas vrbada";

        int pageNumber = 0;
        int pageSize = 2;

        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<User> page = userRepository.findAll(keyword, pageable);
        List<User> listUsers = page.getContent();
        listUsers.forEach(System.out::println);

        assertThat(listUsers.size()).isGreaterThan(0);
    }
}
