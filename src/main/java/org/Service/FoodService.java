package org.Service;

import org.entity.Food;
import org.entity.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FoodService extends Service{
    @Override
    public Connection connection() {
        return super.connection();
    }
    //get all foods from database
    public List<Food> getAll(){
        List<Food> foodList=new ArrayList<>();
        try {
            PreparedStatement statement=connection().prepareStatement("select * from food");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()){
                Food food=Food.builder()
                        .id(resultSet.getLong("id"))
                        .category_id(resultSet.getLong("category_id"))
                        .description(resultSet.getString("description"))
                        .price(resultSet.getLong("price"))
                        .img_link(resultSet.getString("img_link"))
                        .name(resultSet.getString("name"))
                        .build();
                foodList.add(food);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return foodList;
    }
    //find info food from database
    public Food findUser(String foodName){
        Food food=null;
        try {
            PreparedStatement statement= connection().prepareStatement("select * from food where name=?");
            statement.setString(1,foodName);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()){
                food=Food.builder()
                        .id(resultSet.getLong("id"))
                        .category_id(resultSet.getLong("category_id"))
                        .description(resultSet.getString("description"))
                        .price(resultSet.getLong("price"))
                        .img_link(resultSet.getString("img_link"))
                        .name(resultSet.getString("name"))
                        .build();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }return food;
    }
}
