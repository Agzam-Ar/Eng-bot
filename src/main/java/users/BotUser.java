package users;

import java.util.ArrayList;
import java.util.List;

import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import main.Bot;
import spotlight.Manager;

public class BotUser {

	public static final int ROLE_TEACHER = 0;
	public static final int ROLE_STUDENT = 1;
	public static final int ROLE_USER = 2;
	
	private static final String CLASS_NAME = "className";
	
	User user;
	Bot bot;
	
	String classOfUser = null;
	
	String needInputData = "";
	
	int role = -1;
	
	public BotUser(Bot bot, User user) {
		this.user = user;
		this.bot = bot;
	}

	/**
	 * getChatID
	 * @return chatID of user
	 */
	public long getChatID() {
		return user.getId();
	}
	

	public String getUserName() {
//        String userName = user.getUserName();
//        if(userName != null) return userName;
        String name = " ";
        if(user.getLastName() != null) name = name + user.getLastName();
        if(user.getFirstName() != null) name = user.getFirstName() + name;
        return name;
    }

	public String getShotName(String txt) {
		return "[" + txt + "](tg://user?id=" + getChatID() + ")";
	}
	
	String HELP = "Команды:\n"
					+ "/start - начать\n"
					+ "/help - помощь\n"
					+ "/content - содержание";
	
	private void callbackQuery(CallbackQuery callbackQuery) {
		
		String data = callbackQuery.getData();
		
		if(data.startsWith("role_") && role == -1) {
			if(data.equals("role_teacher")) {
				role = ROLE_TEACHER;
				send("Выдумайте название Вашего класса");
				needInputData = CLASS_NAME;
			}
			if(data.equals("role_student")) {
				role = ROLE_STUDENT;
				send("Введите название класса, в который ты хочешь присоединиться");
				needInputData = CLASS_NAME;
			}
			if(data.equals("role_user")) {
				role = ROLE_USER;
				send("Список команд: /help");
				needInputData = "";
			}
		}
		
		if(data.startsWith("msg;")) {
			String[] dts = data.split(";");
			if(dts.length == 3) {
				try {
					sendTo(dts[2], Long.parseLong(dts[1]));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		if(data.startsWith("addStudent;")) {
			String[] dts = data.split(";");
			if(dts.length == 2) {
				try {
					Long chatId = Long.parseLong(dts[1]);
					Users.getClassByName(classOfUser).addStudent(Users.getUserByID(chatId.longValue()));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		if(data.startsWith("dropStudent;")) {
			String[] dts = data.split(";");
			if(dts.length == 2) {
				try {
					Long chatId = Long.parseLong(dts[1]);
					Users.getClassByName(classOfUser).dropStudent(Users.getUserByID(chatId.longValue()));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void onUpdateReceived(Update update) { // TODO
    	Message msg = update.getMessage();
    	
    	if(update.hasCallbackQuery()) {
    		callbackQuery(update.getCallbackQuery());
    		return;
    	}
    	
    	String msgTxt = msg.getText();
    	String send = "";
    	
    	
    	
		System.out.println(getUserName() + "(" + getChatID() + "):\n" + msgTxt);

		if(msgTxt.equals("/help")) {
			send(HELP);
    		return;
    	}

    	if(msgTxt.equals("/content")) {
    		sendHtml(Manager.getContentHTML());
    		return;
    	}
    	
    	if(msgTxt.equals("/joingroup")) {
			send("Введите название класса, в который ты хочешь присоединиться");
    		needInputData = CLASS_NAME;
    		classOfUser = null;
    		return;
    	}
    	
    	if(Manager.checkLessonCommandAndSend(this, msgTxt)) {
    		return;
    	}
		
		
    	if(role == -1) {
    		send = "Пожалуйста, выберите, кем вы являйтесь:";
    		sendKeyboardChoose(send, new String[] {"Преподаватель", "Ученик", "Пользователь"},
    				 new String[] {"role_teacher", "role_student", "role_user"});
    		return;
    	}
    	
    	if(needInputData.equals(CLASS_NAME)) {
    		if(classOfUser != null) {
        		send("Вы уже состоите в классе " + classOfUser);
        		return;
    		}
    		if(role == ROLE_TEACHER) {
    			String newClassName = "";
    			for (char c : msgTxt.toCharArray()) {
					if(Character.isAlphabetic(c) || Character.isDigit(c)) {
						newClassName += c;
					}
				}
    			boolean isCreated = Users.createClass(this, newClassName);
    			if(isCreated) {
    				send = "Отлично, создан новый класс `" + newClassName + "`";
    				classOfUser = newClassName;
    				needInputData = "";
    			}else {
    				send = "Название класса `" + newClassName + "` уже занято";
    			}
    		} else if(role == ROLE_STUDENT) {
    			String newClassName = "";
    			for (char c : msgTxt.toCharArray()) {
					if(Character.isAlphabetic(c) || Character.isDigit(c)) {
						newClassName += c;
					}
				}
    			Class class1 = Users.getClassByName(newClassName);
    			boolean isFound = class1 != null;
    			if(isFound) {
    				send = "Отлично, запрос на присоедине к классу `" + newClassName + "` отправлен!\n Я сообщю, когда учитель добавит Вас";
    				classOfUser = newClassName;
    				class1.addStudentToCheck(this);
    				needInputData = "";
    			}else {
    				send = "Я не нашел класса с названием `" + newClassName + "`";
    			}
    		}
    	}
    	send(send);
	}

	public void sendHtml(String text) {
        SendMessage answer = new SendMessage();
        answer.setText(text);
        answer.setParseMode("html");
        sendMessage(answer);
    }
	
	public void send(String text) {
        SendMessage answer = new SendMessage();
        answer.setText(text);
        answer.setParseMode("markdown");
        sendMessage(answer);
    }
	
	public void sendTo(String text, Long id) {
        SendMessage message = new SendMessage();
        message.setText(text);
        message.setParseMode("markdown");
        message.setChatId(id.toString());
        if(message.getText().isEmpty()) {
        	return;
        }
        try {
			bot.execute(message);
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
    }
	
	private void sendMessage(SendMessage message) {
        message.setChatId(getChatID() + "");
        if(message.getText().isEmpty()) {
        	return;
        }
        try {
			bot.execute(message);
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
    }
	
	public void sendDocument(SendDocument message) {
		message.setChatId(getChatID() + "");
        try {
			bot.execute(message);
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}
    
	public void sendKeyboardChoose(String text, String[] keyboard, String[] callbackData) {
		SendMessage message = new SendMessage();
		message.setParseMode("markdown");
        
		InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<List<InlineKeyboardButton>>();
        for (int i = 0; i < keyboard.length; i++) {
        	List<InlineKeyboardButton> rowInline = new ArrayList<InlineKeyboardButton>();
            InlineKeyboardButton button = new InlineKeyboardButton(text);
            button.setText(keyboard[i]);
            button.setCallbackData(callbackData[i]);
            rowInline.add(button);
            rowsInline.add(rowInline);
		}
        markupInline.setKeyboard(rowsInline);
        message.setReplyMarkup(markupInline);
        
        message.setText(text);
        sendMessage(message);
	}
	
	public void setRole(int role) {
		this.role = role;
	}
	
	public void setClassOfUser(String classOfUser) {
		this.classOfUser = classOfUser;
	}
}
