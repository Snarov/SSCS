	package clientSnarovIA.viewSnarovIA;

import clientSnarovIA.Client;
import javax.swing.*;
import java.awt.*;
import java.util.regex.Pattern;
import java.net.*;

//компонент представления, в котором происходит подключение к серверу
public class AuthorizationComponent extends JComponent {

	//константы
	private static final String HOST_LABEL = "Адрес:номер порта: ";
	private static final String PASSWORD_LABEL = "Пароль: ";
	private static final String BTN_TXT = "Подключиться";
	private static final short COLS = 15;		//кол-во символов в полях
	
	private static final String HOST_PATTERN = "(\\d{1,3}\\.){3}\\d{1,3}:\\d{1,4}";
	private static final String PASSWORD_PATTERN = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?!.*\\s).*$";
	
	//поля
	private final JTextField hostField = new JTextField(COLS);
	private final JPasswordField passwordField = new JPasswordField(COLS);
	private final JButton confirmBtn = new JButton(BTN_TXT);
	
	private boolean isCorrectHost;
	private boolean isCorrectPassword;

	//конструкторы
	public AuthorizationComponent() {
		JPanel centralPanel = new JPanel(new BorderLayout());			//панель, выводимая по центру

		centralPanel.add(new JPanel() {		//добавить поле адреса с лэйблом
			{
				add(new JLabel(HOST_LABEL));
				add(hostField);
			}
		},
				SwingConstants.NORTH);

		centralPanel.add(new JPanel() {		//добавить поле пароля с лэйблом
			{
				add(new JLabel(PASSWORD_LABEL));
				add(passwordField);
			}
		},
				SwingConstants.SOUTH);
		
		//добавление обработчиков событий ввода текста для активации/деактивации кнопки подключения
		hostField.addActionListener((e) -> {
			isCorrectHost = Pattern.matches(HOST_PATTERN, hostField.getText());
			setBtnMode();
		});
		
		passwordField.addActionListener((e) -> {
			isCorrectPassword = Pattern.matches(PASSWORD_PATTERN, passwordField.getText());
			setBtnMode();
		});
		
		add(centralPanel);			//добавить центральную панель в центр компонента
		
		confirmBtn.addActionListener((e) -> {		//при нажатии на кнопку отправляем серверу данные
			String[] hostParts = hostField.getText().split(":");
			if(Client.authorize(new InetSocketAddress(hostParts[0], Integer.parseInt(hostParts[1])), passwordField.getText()))
				//TO DO 
		});

	}
	
	//методы
	private void setBtnMode(){		//устаавливает режим активности кнопки в зависимости от флагов корректности ввода
		confirmBtn.setEnabled(isCorrectHost && isCorrectPassword);
	}
}
