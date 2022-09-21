package org.Service;

import org.entity.Food;
import org.entity.User;

import java.sql.*;
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

    public List<Food> getAll(String categoryName) {
        List<Food> foodList = new ArrayList<>();
        try {
            PreparedStatement statement = null;
            statement = connection().prepareStatement("select f.name from food f\n" +
                    "inner join category c on c.id=f.category_id\n" +
                    "where c.name=?");
            statement.setString(1, categoryName);

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Food food = Food.builder()
                        .name(resultSet.getString("name"))
                        .build();

                foodList.add(food);
            }

            return foodList;
        } catch (SQLException e) {
            System.out.println("Not saved");
            return foodList;
        }
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
