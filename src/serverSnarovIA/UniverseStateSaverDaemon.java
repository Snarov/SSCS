package serverSnarovIA;

import serverSnarovIA.modelSnarovIA.physicsSnarovIA.PhysicalUniverse;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.File;

//поток-демон сохранения состояния виртуальной вселенной
public class UniverseStateSaverDaemon extends Thread {

	//константы
	private static final int SAVE_INTERVAL = 5000;				//интервал сохранения состояния
	private static final String SAVENAME = "Universe.sav";		//имя файла сохранения
	private static final String SAVE_ERR_WARN = "Warning: ошибка сохранения состояния";
	//поля
	private String saveDir = "";	//директория с сохраняемым файлом
	private final PhysicalUniverse savingUniverse;

	//конструкторы
	public UniverseStateSaverDaemon(PhysicalUniverse aSavingUniverse, String aSaveDir) {
		savingUniverse = aSavingUniverse;

		if (aSaveDir == null)
			saveDir = aSaveDir;

		setDaemon(true);
	}

	//поведение
	@Override
	public void run() {			//сохраняет состояние вселенной через некоторый период времени
		File saveFile = new File(saveDir + "/" + SAVENAME);

		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(saveFile))) {
			while (true) {
				Thread.sleep(SAVE_INTERVAL);
				if (saveFile.exists())		//суперспособ очистки удалением и вновь созданием :)
					saveFile.delete();
				saveFile.createNewFile();
				synchronized (savingUniverse) {
					oos.writeObject(savingUniverse);
				}

			}
		} catch (IOException ex) {
			System.err.println(SAVE_ERR_WARN);
		} catch (InterruptedException ex) {
		}
	}
}
