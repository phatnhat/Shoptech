package com.shoptech.admin.user;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import com.shoptech.common.entity.Role;
import org.springframework.test.annotation.Rollback;

import java.util.List;

@DataJpaTest
// chú thích @AutoConfigureTestDatabase được sử dụng để cấu hình cơ sở dữ liệu trong quá trình kiểm thử. Tham số replace trong chú thích này xác định cách Spring Boot xử lý cấu hình cơ sở dữ liệu tự động.
// nếu ko có annotation này nó sẽ tự động thay thế cấu hình cơ sở dữ liệu bằng một cơ sở dữ liệu nhớ trong bộ nhớ (in-memory database) để sử dụng trong quá trình kiểm thử.
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(value = false) // Bất kỳ thay đổi nào được thực hiện trên cơ sở dữ liệu trong quá trình kiểm thử sẽ được commit và lưu trữ nếu để giá trị là false nếu true sẽ tự động rollback và ko commit vào db
public class RoleRepositoryTests {
    @Autowired
    RoleRepository roleRepository;

    @Test
    public void testCreateFirstRole(){
        Role roleAdmin = new Role("Admin", "Manage everything");
        Role savedRole = roleRepository.save(roleAdmin);
        assertThat(savedRole.getId()).isGreaterThan(0);
    }

    @Test
    public void testCreateRestRoles(){
        Role roleSalespersion = new Role("Salesperson",
                "Manage product price, customers, shipping, orders and sales report");
        Role roleEditor = new Role("Editor",
                "Manage categories, brands, product, articles and menus");
        Role roleShipper = new Role("Shipper",
                "View products, view orders and update order status");
        Role roleAssistant = new Role("Assistant",
                "Manage questions and reviews");

        roleRepository.saveAll(List.of(roleSalespersion, roleEditor, roleAssistant, roleShipper));
    }
}
