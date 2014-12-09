package serverSnarovIA;

import java.io.Serializable;

//экземпляры классов, реализующих этот интерфейс, отправляются клиенту в качестве датаграмм
public interface SendingInfo extends Serializable{
	void refresh();			//обновить информацию
}
