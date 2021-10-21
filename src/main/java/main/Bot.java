package main;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import users.BotUser;
import users.Users;

public class Bot extends TelegramLongPollingBot {
	
    private String BOT_NAME;
	private String BOT_TOKEN;

	public Bot(String botName, String botToken) {
        super();
        this.BOT_NAME = botName;
        this.BOT_TOKEN = botToken;

//        BotUser agzam = new BotUser(this, new User(1305677163l, "null_nulll", false));
//        agzam.setRole(BotUser.ROLE_TEACHER);
//        agzam.setClassOfUser("9г");
//        
//        Users.adUsers(agzam);
//        
//        Users.createClass(agzam, "9г");
        
    }
    
    public void onUpdateReceived(Update update) {
    	try {
    		chat(update);
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }

    private void chat(Update update) { // TODO chat
    	Message msg = update.getMessage();
    	Long chatId = null;
    	if(msg != null) {
    		chatId = msg.getChatId();
    		User tUser = msg.getFrom();
    		Users.createUser(this, tUser);
    	}else if(update.hasCallbackQuery()){
    		chatId = update.getCallbackQuery().getFrom().getId();
    	}
    	if(chatId == null) return;
    	
    	BotUser user = Users.getUserByID(chatId);

    	if(user == null) return;
    	user.onUpdateReceived(update);
    }
    
    private void setAnswerMarkdown(Long chatId, String text) {
        SendMessage answer = new SendMessage();
        answer.setText(text);
        answer.setParseMode("markdown"); // markdown
        answer.setChatId(chatId.toString());
        try {
			execute(answer);
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
    }
    
    
    /**
     * Отправка ответа
     * @param chatId id чата
     * @param text текст ответа
     */
	
    public void setAnswer(Long chatId, String text) { 	
        SendMessage answer = new SendMessage();
        answer.setParseMode("markdown");
        answer.setText(text);
        answer.setChatId(chatId.toString());
        try {
            execute(answer);
        } catch (TelegramApiException e) {
        }
    }

	@Override
	public String getBotUsername() {
		return BOT_NAME;
	}

	@Override
	public String getBotToken() {
		return BOT_TOKEN;
	}
}
