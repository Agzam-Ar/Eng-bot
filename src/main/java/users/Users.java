package users;

import java.util.ArrayList;

import org.telegram.telegrambots.meta.api.objects.User;

import main.Bot;

public class Users {

	private static ArrayList<BotUser> users;
	private static ArrayList<Class> classes;
	
	public static void init() {
		users = new ArrayList<BotUser>();
		classes = new ArrayList<Class>();
	}
	
	public static void adUsers(BotUser botUser) {
		users.add(botUser);
	}
	
	/**
	 * @param user
	 * @return - true - user created, false - not created
	 */
	public static boolean createUser(Bot bot, User user) {
		if(getUserByID(user.getId()) == null) {
			users.add(new BotUser(bot, user));
			return true;
		}
		return false;
	}
	
	/**
	 * @param id - chat id
	 * @return BotUser, that have selected id. If BotUser not found - return null
	 */
	public static BotUser getUserByID(long id) {
		for (BotUser botUser : users) {
			if(botUser.getChatID() == id) return botUser;
		}
		return null;
	}
	
	/**
	 * @param teacher - teacher
	 * @param name - name of class
	 * @return - true - user created, false - not created
	 */
	public static boolean createClass(BotUser teacher, String name) {
		if(getClassByName(name) == null) {
			classes.add(new Class(name, teacher));
			return true;
		}
		return false;
	}
	
	/**
	 * @param id - chat id
	 * @return Class, that have selected name. If Class not found - return null
	 */
	public static Class getClassByName(String name) {
		for (Class myClass : classes) {
			if(myClass.getName().equals(name)) return myClass;
		}
		return null;
	}
}
