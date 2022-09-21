package org.bot;

import org.Service.CategoryService;
import org.Service.FoodService;
import org.Service.UserService;
import org.entity.Category;
import org.entity.Food;
import org.entity.Order;
import org.entity.User;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ApplicationBot extends TelegramLongPollingBot {
    private final String USERNAME="FastFodBot";
    private final String TOKEN="5474926697:AAFPDoeT8YMRxIoBFJzeojT9Eug_ekk8dK8";
    @Override
    public String getBotUsername() {
        return USERNAME;
    }

    @Override
    public String getBotToken() {
        return TOKEN;
    }
    List<Order>orders=new ArrayList<>();
    Order order=new Order();
    UserService userService=new UserService();
    FoodService foodService=new FoodService();
    CategoryService categoryService=new CategoryService();

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()&&update.getMessage()!=null){
            Message message=update.getMessage();
            Long chatId=message.getChatId();
            User user=userService.findUser(chatId);
            System.out.println(message.getMessageId());
            if(user==null){
                userService.saveUser(chatId);
                user=userService.findUser(chatId);
            }
            if (message.hasText()){
                String text=message.getText();
                if (text.equals("/start") && user.getPhoneNumber()==null){
                    sendMessageWithBTN("Botga xush kelibsiz\nTelefon raqamingizni yuboring",chatId,getPhoneBTN());
                } else if (text.equals("/start")) {

                    sendMessageWithBTN("Bosh sahifa",chatId,getHomeBTN());
                    userService.changeAction(chatId,"START");

                } else if (user.getAction().equals("START") && text.equals("Menyu")) {

                    sendMessageWithBTN("Quyidagi kategorylardan birini tanlang", chatId, getCategories());
                    userService.changeAction(chatId,"CATEGORY");

                } else if (user.getAction().equals("CATEGORY")) {
                    if (text.equals("Asosiyga qaytish")){
                        sendMessageWithBTN("Asosiy sahifa",chatId,getHomeBTN());
                        userService.changeAction(chatId,"START");
                    }else {
                        sendMessageWithBTN("Kerakli tugmani bosing", chatId, getFoods(text, chatId));
                        userService.changeAction(chatId, "FOOD");
                    }

                } else if (user.getAction().equals("FOOD")) {
                    if (text.equals("Ortga")){
                        sendMessageWithBTN("Quyidagi kategorylardan birini tanlang", chatId, getCategories());
                        userService.changeAction(chatId,"CATEGORY");
                    }else if (text.equals("Asosiyga qaytish")){
                        sendMessageWithBTN("Asosiy sahifa",chatId,getHomeBTN());
                        userService.changeAction(chatId,"START");
                    }else {
                        order=new Order();
                        order.setName(text);
                        Food food=foodService.findUser(text);
                        sendPhoto(food.getDescription()+"\nNarxi:"+food.getPrice(),food.getImg_link(),chatId,getFoodCount(order.getCount()));
                        order.setPrice(food.getPrice());
                    }

                } else if (user.getAction().equals("START") && text.equals("Savatcha")) {
                    StringBuilder builder=new StringBuilder();
                    double allPrice=0;
                    for (Order order1 : orders) {
                        builder.append("\n"+order1.getName()+" "+order1.getPrice()+"so'm \n"
                        +order1.getCount()+" dona jami "+order1.getAllPrice()+" so'm\n");
                        allPrice+=order1.getAllPrice();
                    }
                    builder.append("\n Umumiy hisob: "+(allPrice));
                    sendInline(builder.toString(),chatId,basketButton());
                }
            } else if (message.hasContact()) {
                Contact contact=message.getContact();
                System.out.println(contact.getPhoneNumber());
                user.setPhoneNumber(contact.getPhoneNumber());
                System.out.println(userService.updatePhoneNumber(contact.getPhoneNumber(), chatId));
                sendMessageWithBTN("Bosh sahifa",chatId,getHomeBTN());
            }

        } else if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery=update.getCallbackQuery();
            String data = callbackQuery.getData();
            if (data.equals("+")){
                order.setCount(order.getCount()+1);
                EditMessageReplyMarkup editMessageReplyMarkup=new EditMessageReplyMarkup();
                editMessageReplyMarkup.setChatId(callbackQuery.getMessage().getChatId());
                editMessageReplyMarkup.setMessageId(callbackQuery.getMessage().getMessageId());
                editMessageReplyMarkup.setReplyMarkup(getFoodCount(order.getCount()));

                try {
                    execute(editMessageReplyMarkup);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }



            } else if (data.equals("-")&&order.getCount()>1) {
                order.setCount(order.getCount()-1);
                EditMessageReplyMarkup editMessageReplyMarkup=new EditMessageReplyMarkup();
                editMessageReplyMarkup.setChatId(callbackQuery.getMessage().getChatId());
                editMessageReplyMarkup.setMessageId(callbackQuery.getMessage().getMessageId());
                editMessageReplyMarkup.setReplyMarkup(getFoodCount(order.getCount()));

                try {
                    execute(editMessageReplyMarkup);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }


            } else if (data.equals("basket")) {
                order.setAllPrice(order.getCount()*order.getPrice());
                orders.add(order);
                SendMessage sendMessage=new SendMessage();
                sendMessage.setText("Savatga qo'shildi");
                sendMessage.setChatId(callbackQuery.getMessage().getChatId());

                try {
                    execute(sendMessage);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }

            } else if (data.equals("back")) {
                sendMessageWithBTN("Bosh sahifa",callbackQuery.getMessage().getChatId(),getHomeBTN());
            } else if (data.equals("clean")) {
                if(orders.isEmpty()){
                    sendMessageWithBTN("Bosh sahifa",callbackQuery.getMessage().getChatId(),getHomeBTN());
                }else {
                orders.clear();
                SendMessage sendMessage=new SendMessage();
                sendMessage.setText("Savatcha tozalandi");
                sendMessage.setChatId(callbackQuery.getMessage().getChatId());

                try {
                    execute(sendMessage);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
                }
            }
        }


    }
    public InlineKeyboardMarkup getFoodCount(int count){
        InlineKeyboardMarkup markup=new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>>rows=new ArrayList<>();
        List<InlineKeyboardButton> row=new ArrayList<>();

        InlineKeyboardButton button=new InlineKeyboardButton();
        button.setText("-");
        button.setCallbackData("-");// data=-count:1
        row.add(button);

        button=new InlineKeyboardButton();
        button.setText(String.valueOf(count));
        button.setCallbackData("count:"+count);
        row.add(button);

        button=new InlineKeyboardButton();
        button.setText("+");
        button.setCallbackData("+");
        row.add(button);

        rows.add(row);
        row=new ArrayList<>();

        button=new InlineKeyboardButton();
        button.setText("savatchaga saqlash");
        button.setCallbackData("basket");
        row.add(button);

        rows.add(row);
        markup.setKeyboard(rows);

        return markup;
    }
    public void sendPhoto(String text, String image_link, Long chatId, InlineKeyboardMarkup markup){
        SendPhoto sendPhoto=new SendPhoto();
        sendPhoto.setChatId(chatId);
        sendPhoto.setCaption(text);
        sendPhoto.setReplyMarkup(markup);
        sendPhoto.setPhoto(new InputFile(new File(image_link)));

        try {
            execute(sendPhoto);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }

    }
    public void sendInline(String text, Long chatId, InlineKeyboardMarkup markup){
        SendMessage sendMessage=new SendMessage();
        sendMessage.setText(text);
        sendMessage.setChatId(chatId);
        sendMessage.setReplyMarkup(markup);

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }

    }

    public void sendMessageWithBTN(String text, Long chatId, ReplyKeyboardMarkup markup) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);
        sendMessage.setReplyMarkup(markup);

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public ReplyKeyboardMarkup getPhoneBTN() {
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setResizeKeyboard(true);
        List<KeyboardRow> rows = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();

        KeyboardButton button = new KeyboardButton();
        button.setText("Telefon raqamni yuborish");
        button.setRequestContact(true);

        row.add(button);
        rows.add(row);
        markup.setKeyboard(rows);
        return markup;
    }
    public ReplyKeyboardMarkup getHomeBTN() {
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setResizeKeyboard(true);
        List<KeyboardRow> rows = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();

        KeyboardButton button = new KeyboardButton();
        button.setText("Menyu");
        row.add(button);

        button = new KeyboardButton();
        button.setText("Savatcha");
        row.add(button);
        rows.add(row);

        row = new KeyboardRow();
        button = new KeyboardButton();
        button.setText("Joy band qilish");
        row.add(button);
        rows.add(row);

        markup.setKeyboard(rows);
        return markup;
    }
    public ReplyKeyboardMarkup getCategories() {

        List<Category> categories = categoryService.getAll();
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setResizeKeyboard(true);
        List<KeyboardRow> rows = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();

        for (int i = 0; i < categories.size(); i++) {
            KeyboardButton button = new KeyboardButton();
            button.setText(categories.get(i).getName());
            row.add(button);
            if ((i + 1) % 2 == 0) {
                rows.add(row);
                row = new KeyboardRow();
            }
        }
        row = new KeyboardRow();
        if (categories.size() % 2 == 1) {
            KeyboardButton button = new KeyboardButton();
            button.setText(categories.get(categories.size() - 1).getName());
            row.add(button);
            KeyboardButton button1=new KeyboardButton("Asosiyga qaytish");
            row.add(button1);
            rows.add(row);
        }else {
        row=new KeyboardRow();
        KeyboardButton button=new KeyboardButton("Asosiyga qaytish");
        row.add(button);
        rows.add(row);}

        markup.setKeyboard(rows);
        return markup;
    }
    public ReplyKeyboardMarkup getFoods(String categoryName,Long chatId) {

        List<Food> foodList = foodService.getAll(categoryName);
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setResizeKeyboard(true);
        List<KeyboardRow> rows = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();

        for (int i = 0; i < foodList.size(); i++) {
            KeyboardButton button = new KeyboardButton();
            button.setText(foodList.get(i).getName());
            row.add(button);
            if ((i + 1) % 2 == 0) {
                rows.add(row);
                row = new KeyboardRow();
            }
        }
        row = new KeyboardRow();
        if (foodList.size() % 2 == 1) {
            KeyboardButton button = new KeyboardButton();
            button.setText(foodList.get(foodList.size() - 1).getName());
            row.add(button);
            KeyboardButton button1=new KeyboardButton("Ortga");
            row.add(button1);
            rows.add(row);
            row=new KeyboardRow();
            KeyboardButton button2=new KeyboardButton("Asosiyga qaytish");
            row.add(button2);
            rows.add(row);
        }else {
            row=new KeyboardRow();
            KeyboardButton button=new KeyboardButton("Ortga");
            KeyboardButton button2=new KeyboardButton("Asosiyga qaytish");
            row.add(button);
            row.add(button2);
            rows.add(row);  
        }

        markup.setKeyboard(rows);
        return markup;

    }
    public InlineKeyboardMarkup basketButton(){
        InlineKeyboardMarkup markup=new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>>rows=new ArrayList<>();
        List<InlineKeyboardButton> row=new ArrayList<>();

        InlineKeyboardButton button=new InlineKeyboardButton();
        button.setText("Orqaga");
        button.setCallbackData("back");
        row.add(button);
        button=new InlineKeyboardButton();
        button.setText("Dostavka");
        button.setCallbackData("delivery");
        row.add(button);
        rows.add(row);
        row=new ArrayList<>();
        button=new InlineKeyboardButton();
        button.setText("Tozalash");
        button.setCallbackData("clean");
        row.add(button);
        rows.add(row);

        markup.setKeyboard(rows);

        return markup;


    }

}
