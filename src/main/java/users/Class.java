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
			user.send("�� ���� ��������� � " + name);
			teacher.send("�� �������� ������� � " + name);
		}
	}
	
	public void dropStudent(BotUser user) {
		if(studentToCheck.remove(user)) {
			user.send("��� �� ����� � " + name + "\n/joingroup");
			teacher.send("�� ������� �������");
		}
	}
	
	public void addStudentToCheck(BotUser user) {
		studentToCheck.add(user);
		
		teacher.sendKeyboardChoose(
				user.getShotName("������") + " ����� �������������� � ������ ������",
				new String[] {"��������", "�������"},
				new String[] {
						"addStudent;" + user.getChatID(), 	
						"dropStudent;" + user.getChatID()
						});
	}
}
