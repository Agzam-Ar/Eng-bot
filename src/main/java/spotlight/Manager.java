package spotlight;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.User;

import users.BotUser;

public class Manager {

	private static final String PATH = System.getProperty("user.dir") + "\\data\\MODULES";

	public static ArrayList<String> paths = new ArrayList<String>();
	
	public static final String[][] MODULES = loadModules();
	public static final String[] MODULE_NAMES = loadModulNames();
	public static final int[][] MODULE_PATH_INDEX = loadModulIndex();


	private static int[][] loadModulIndex() {
		File file = new File(PATH);
		System.out.println(PATH);
		int[][] mod = new int[file.listFiles().length][];
		int index = 0;
		File[] files = file.listFiles();
		for (int i = 0; i < files.length; i++) {
			File[] fs = files[i].listFiles();
			mod[i] = new int[fs.length];
			for (int j = 0; j < fs.length; j++) {
				mod[i][j] = index;
				System.out.println(fs[j].getPath());
				paths.add(fs[j].getPath());
				index++;
			}
		}
		return mod;
	}
	private static String[][] loadModules() {
		File file = new File(PATH);
		System.out.println(PATH);
		String[][] mod = new String[file.listFiles().length][];
		File[] files = file.listFiles();
		for (int i = 0; i < files.length; i++) {
			File[] fs = files[i].listFiles();
			mod[i] = new String[fs.length];
			for (int j = 0; j < fs.length; j++) {
				mod[i][j] = fs[j].getName();
			}
		}
		return mod;
	}
	
	private static String[] loadModulNames() {
		File file = new File(PATH);
		String[] modNames = new String[file.listFiles().length];
		File[] files = file.listFiles();
		for (int i = 0; i < files.length; i++) {
			modNames[i] = files[i].getName();
		}
		return modNames;
	}
	
	public static String toCommand(String name) {
		return name.replaceAll(" \\+ ", "_p_").replaceAll(" ", "_");
	}
	
	
	public static boolean checkLessonCommandAndSend(BotUser user, String msgText) {
		System.out.println(msgText + " - " + msgText.startsWith("/lesson"));
		if(msgText.startsWith("/lesson")) {
			String sub = msgText.replaceFirst("/lesson", "");
			try {
				int index = Integer.parseInt(sub);
				File file = new File(paths.get(index));
				System.out.println("file: " + file.getPath());
				for (File f : file.listFiles()) {
					System.out.println("f: " + f.getPath());
					if(f.getPath().endsWith(".html")) {
					    SendDocument sendDocumentRequest = new SendDocument();
					    sendDocumentRequest.setChatId(user.getChatID() + "");
					    sendDocumentRequest.setDocument(new InputFile(f));
					    sendDocumentRequest.setCaption("ƒл€ большего удобства используйте ѕ , а так же Chrome-браузеры (яндекс, Google Chrome и тп..)");
					    user.sendDocument(sendDocumentRequest);
						return true;
					}
				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	public static String getContentHTML() {
		String content = "<b><u>Content:</u></b>";
		for (int i = 0; i < MODULES.length; i++) {
			if(MODULES[i].length > 0) {
				content += "\n<b><u>" + MODULE_NAMES[i] + "</u></b>\n";
				for (int j = 0; j < MODULES[i].length; j++) {
					content += MODULES[i][j] + ": /lesson" + MODULE_PATH_INDEX[i][j] + "\n";
				}
			}
		}
		return content;
	}
}
