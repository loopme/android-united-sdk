package com.loopme.xml.vast4;

import com.loopme.parser.xml.Tag;

import java.util.List;

public class Categories {
    @Tag("Category")
    private List<Category> categoryList;

    public List<Category> getCategoryList() {
        return categoryList;
    }
}

