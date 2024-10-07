package com.shoptech.admin.paging;

import com.shoptech.admin.user.UserService;
import com.shoptech.common.entity.Brand;
import com.shoptech.common.entity.User;
import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.List;

public class PagingAndSortingHelper {
    private final ModelAndViewContainer model;
    private final String listName;
    @Getter
    private final String sortField;
    @Getter
    private final String sortDir;
    @Getter
    private final String keyword;

    public PagingAndSortingHelper(ModelAndViewContainer model, String listName, String sortField, String sortDir, String keyword) {
        this.model = model;
        this.listName = listName;
        this.sortField = sortField;
        this.sortDir = sortDir;
        this.keyword = keyword;
    }

    public void updateModelAttributes(int pageNum, Page<?> page){
        List<?> listItems = page.getContent();
        int pageSize = page.getSize();

        int startCount = (pageNum - 1) * pageSize + 1;
        int endCount = startCount + pageSize - 1;
        if(endCount > page.getTotalElements()) endCount = page.getTotalPages();

        model.addAttribute("currentPage", pageNum);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute(listName, listItems);
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("totalItems", page.getTotalElements());
    }

    public void listEntities(int pageNum, int pageSize, SearchRepository<?, Long> repo){
        Sort sort = Sort.by(sortField);
        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();

        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, sort);
        Page<?> page = null;

        if(keyword != null)  page = repo.findAll(keyword, pageable);
        else page = repo.findAll(pageable);

        updateModelAttributes(pageNum, page);
    }

    public Pageable createPageable(int pageSize, int pageNum) {
        Sort sort = Sort.by(sortField);
        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
        return PageRequest.of(pageNum - 1, pageSize, sort);
    }
}
