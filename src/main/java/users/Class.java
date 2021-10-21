package users;

import java.util.ArrayList;

public class Class {

	ArrayList<BotUser> student;
	ArrayList<BotUser> studentToCheck;
	BotUser teacher;
	String name;
	
	public Class(String name, BotUser teacher) {
		this.teacher = teacher;
		this.name = name;

		student = new ArrayList<BotUser>();
		studentToCheck = new ArrayList<BotUser>();
	}
	
	public String getName() {
		return name;
	}
	
	public void addStudent(BotUser user) {
		if(studentToCheck.remove(user)) {
			student.add(user);
			user.send("Вы были добавлены в " + name);
			teacher.send("Вы добавили ученика в " + name);
		}
	}
	
	public void dropStudent(BotUser user) {
		if(studentToCheck.remove(user)) {
			user.send("Вас не взяли в " + name + "\n/joingroup");
			teacher.send("Вы удалили ученика");
		}
	}
	
	public void addStudentToCheck(BotUser user) {
		studentToCheck.add(user);
		
		teacher.sendKeyboardChoose(
				user.getShotName("Ученик") + " хочет присоединиться к Вашему классу",
				new String[] {"Добавить", "Удалить"},
				new String[] {
						"addStudent;" + user.getChatID(), 	
						"dropStudent;" + user.getChatID()
						});
	}
}
