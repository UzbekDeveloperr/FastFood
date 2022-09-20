package org.Main;

import org.Service.CategoryService;

public class Main {
    public static void main(String[] args) {
        CategoryService categoryService=new CategoryService();
        categoryService.getAll();
    }
}