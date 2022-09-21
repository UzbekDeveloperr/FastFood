package org.Service;

import org.entity.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserService extends Service{
    @Override
    public Connection connection() {
        return super.connection();
    }
    //find user from datebase
    public User findUser(Long chatId){
        User user=null;
        try {
            PreparedStatement statement= connection().prepareStatement("select * from users where chat_id=?");
            statement.setLong(1,chatId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()){
                user=User.builder()
                        .chatId(resultSet.getLong("chat_id"))
                        .action(resultSet.getString("action"))
                        .phoneNumber(resultSet.getString("phoneNumber"))
                        .build();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }return user;
    }
    //save user to database
    public boolean saveUser(Long chatId){
        try {
            PreparedStatement statement= connection().prepareStatement("insert into users(chat_id) values(?)");
            statement.setLong(1,chatId);
            return !statement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    //update phone number user
    public boolean  updatePhoneNumber(String phoneNumber,Long chatId){
        try {
            PreparedStatement statement= connection().prepareStatement("update users set \"phoneNumber\"=? where chat_id=?");
            statement.setString(1,phoneNumber);
            statement.setLong(2,chatId);
            return !statement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    //change action user
    public boolean changeAction(Long chatId,String action){
        try {
            PreparedStatement statement= connection().prepareStatement("update users set \"action\"=? where chat_id=?");
            statement.setString(1,action);
            statement.setLong(2,chatId);
            return !statement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
