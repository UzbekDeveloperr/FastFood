package org.Service;

import org.entity.Category;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CategoryService extends Service{
    @Override
    public Connection connection() {
        return super.connection();
    }
    public List<Category> getAll(){
        List<Category> categories=new ArrayList<>();
        try {
            PreparedStatement statement=connection().prepareStatement("select * from category");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Category category = Category.builder()
                        .id(resultSet.getLong("id"))
                        .name(resultSet.getString("name"))
                        .build();

                categories.add(category);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return categories;
    }
}
