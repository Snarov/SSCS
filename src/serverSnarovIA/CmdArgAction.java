package serverSnarovIA;

//представляет собой функциональный интерфейс обработки значения аргумента командной строки
@FunctionalInterface
interface CmdArgAction {

	public void process(String arg);
}
