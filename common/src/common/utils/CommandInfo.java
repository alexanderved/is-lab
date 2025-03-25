package common.utils;

/**
 * Информация о команде.
 */
public enum CommandInfo {
    HELP("help", new String[] {}, "вывести справку по доступным командам"),
    INFO("info", new String[] {}, "вывести в стандартный поток вывода информацию о коллекции"),
    SHOW("show", new String[] {},
            "вывести в стандартный поток вывода все элементы коллекции в строковом представлении"),
    ADD("add", new String[] { "{element}" }, "добавить новый элемент в коллекцию"),
    UPDATE("update", new String[] { "id", "{element}" },
            "обновить значение элемента коллекции, id которого равен заданному"),
    REMOVE_BY_ID("remove_by_id", new String[] { "id" }, "удалить элемент из коллекции по его id"),
    CLEAR("clear", new String[] {}, "очистить коллекцию"),
    EXECUTE_SCRIPT("execute_script", new String[] { "file_name" }, "считать и исполнить скрипт из указанного файла"),
    EXIT("exit", new String[] {}, "завершить работу клиентского приложения"),
    ADD_IF_MAX("add_if_max", new String[] { "{element}" }, "добавить новый элемент в коллекцию,"
            + " если его значение превышает значение наибольшего элемента этой коллекции"),
    ADD_IF_MIN("add_if_min", new String[] { "{element}" }, "добавить новый элемент в коллекцию,"
            + " если его значение меньше, чем у наименьшего элемента этой коллекции"),
    REMOVE_LOWER("remove_lower", new String[] { "{element}" },
            "удалить из коллекции все элементы, меньшие, чем заданный"),
    AVERAGE_OF_EMPLOYEES_COUNT("average_of_employees_count", new String[] {},
            "вывести среднее значение поля employeesCount для всех элементов коллекции"),
    FILTER_STARTS_WITH_FULL_NAME("filter_starts_with_full_name", new String[] { "fullName" },
            "вывести элементы, значение поля fullName которых начинается с заданной подстроки"),
    PRINT_FIELD_DESCENDING_ANNUAL_TURNOVER("print_field_descending_annual_turnover", new String[] {},
            "вывести значения поля annualTurnover всех элементов в порядке убывания");

    private final String name;
    private final String[] args;
    private final String descr;

    private CommandInfo(String name, String[] args, String descr) {
        this.name = name;
        this.args = args;
        this.descr = descr;
    }

    /**
     * Возвращает информацию о команде с данным названием.
     * 
     * @param name название команды
     * @return информацию о команде
     */
    public static CommandInfo fromName(String name) {
        for (CommandInfo cmdInfo : CommandInfo.values()) {
            if (cmdInfo.getName() == name) {
                return cmdInfo;
            }
        }

        throw new IllegalArgumentException("Информация о команде `" + name + "` отсутствует.");
    }

    /**
     * @return название команды
     */
    public String getName() {
        return name;
    }

    /**
     * @return аргументы команды
     */
    public String[] getArguments() {
        return args;
    }

    /**
     * @return описание команды
     */
    public String getDescription() {
        return descr;
    }
}
