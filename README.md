ДЗ 5. От простого к практике

## Задание:
1. Реализуйте удаление пользователей.
2. Подумать, где должен находиться метод createUser из UserView и если получится, вынести его в нужный слой.
3. Вынести логику dao в слой репозитория, а от слоя dao избавится физически(перенести нужный код в класс репозитория, а пакет dao удалить).
На выбор (не обязательно):
4. подумайте как оптимизировать код приложения (например, хэшировать все данные, а в файл писать только при выходе из приложения)
5. Дописать код для оставшихся команд в Commands (можно реализовать сохранение списка USer)
6. ИЛИ ВНЕСИТЕ СВОИ ИЗМЕНЕНИЯ В ПРОЕКТ, КОТОРЫЕ КАЖУТЬСЯ ЛОГИЧНЫМИ ВАМ.

## Решение:
Ход выполнения:
При анализе уже написанного кода я заметил, что не был реализован метод findById, хотя в следующем же методе update реализован поиск по ID.
Я перенес код из метода update в метод findById, а внутри кода update использовал метод findById. Позже заменил подобный же код на метод findById в методе delete.

    @Override
    public Optional<User> findById(Long userId) {
        List<User> users = findAll();
        User user = users.stream()
                .filter(u -> u.getId()
                        .equals(userId))
                .findFirst().orElseThrow(() -> new RuntimeException("User not found"));
        return Optional.of(user);

    @Override
    public Optional<User> update(Long userId, User update) {
        List<User> users = findAll();
        User editUser = findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        editUser.setFirstName(update.getFirstName());
        editUser.setLastName(update.getLastName());
        editUser.setPhone(update.getPhone());
        write(users);
        return Optional.of(update);

## 1.
Был дописан метод delete, который должен искать в списке нужного юзера и просто удалять его из списка. Список сохранять обратно.
Я посчитал, что возвращать Boolean переменную не имеет большого смысла, поэтому переделал метод на void.

public void delete(Long userId) {
        List<User> users = findAll();
        User deleteUser = findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        users.remove(deleteUser);
        write(users);
    }

## 2.
При анализе кода было решено разбить userView на две логические части:
### - метод createUser был перенесен в класс UserController:
    public User createUser() {
        String firstName = UserDialogue.prompt("Имя: ");
        String lastName = UserDialogue.prompt("Фамилия: ");
        String phone = UserDialogue.prompt("Номер телефона: ");
        return new User(firstName, lastName, phone);
    }
### - остальное было перенесено в отдельный класс UserDialogue:
public class UserDialogue {
    private final UserController userController;
    public UserDialogue(UserController userController) {
        this.userController = userController;
    }
    public void run(){
        Commands com;
        while (true) {
            String command = prompt("------------------------------------\n" +
                    "\tHELP - для подсказки,\n" +
                    "\t LIST - для вывода списка команд,\n" +
                    "\t  EXIT - для завершения программы.\n" +
                    "Введите команду: ");
            com = Commands.valueOf(command);
            if (com == Commands.EXIT) return;
            switch (com) {
                case LIST:
                    System.out.println("Список доступных команд:\n\tREAD\tREADALL\n\tCREATE\n\tUPDATE\n\tDELETE\n\tLIST\n\tEXIT");
                    break;
                case HELP:
                    System.out.println("Данная программа является реализацией телефонной книги для\nзаписи контактов в текстовый файл.\n\n" +
                            "Вы можете создавать новые контакты при помощи команды CREATE.\n" +
                            "\tБудет создан контакт с полями:\n\t\tИмя,\n\t\tФамилия\n\t\tНомер телефона\n" +
                            "Для внесения изменений Вы можете использовать команду UPDATE,\n" +
                            "а для удаления - DELETE.\n" +
                            "Для вывода контактов в виде списка используйте команду READALL.\n" +
                            "По одному - READ.\n\n" +
                            "Для выхода из программы наберите команду EXIT.\n");
                    break;
                case CREATE:
                    User u = userController.createUser();
                    userController.saveUser(u);
                    break;
                case READ:
                    String id = prompt("Идентификатор пользователя: ");
                    try {
                        User user = userController.readUser(Long.parseLong(id));
                        System.out.println(user);
                        System.out.println();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    break;
                case READALL:
                    List<User> users = userController.readAllUsers();
                    for (User user : users) { System.out.println(user); };
                    break;
                case UPDATE:
                    String userIdToUpdate = prompt("Enter user id: ");
                    userController.updateUser(userIdToUpdate, userController.createUser());
                case DELETE:
                    String userIdToDelete = prompt("Enter user id: ");
                    userController.deleteUser(userIdToDelete);
                    break;
            }
        }
    }
    public static String prompt(String message) {
        Scanner in = new Scanner(System.in);
        System.out.print(message);
        return in.nextLine().toUpperCase(Locale.ROOT).replaceAll(" ", "").trim();
    }
}

## 3.
Логика dao перенес в класс UserRepository:

    public UserRepository(String fileName) {
        this.fileName = fileName;
        this.mapper = new UserMapper();
        //this.operation = operation;
    }

    @Override
    public List<String> readAll() {
        List<String> lines = new ArrayList<>();
        try {
            File file = new File(fileName);
            //создаем объект FileReader для объекта File
            FileReader fr = new FileReader(file);
            //создаем BufferedReader с существующего FileReader для построчного считывания
            BufferedReader reader = new BufferedReader(fr);
            // считаем сначала первую строку
            String line = reader.readLine();
            if (line != null) {
                lines.add(line);
            }
            while (line != null) {
                // считываем остальные строки в цикле
                line = reader.readLine();
                if (line != null) {
                    lines.add(line);
                }
            }
            fr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }

Пришлось подставлять имя файла в Main из DBConnector:

import static notebook.util.DBConnector.DB_PATH;
...
        GBRepository repository = new UserRepository(DB_PATH);
        UserController controller = new UserController(repository);
        UserDialogue phoneBook = new UserDialogue(controller);
...

## 4..6
Для задач рефакторинга, оптимизации и улучшения было сделано следующее:
Добавлены варианты команд READALL (вывод полного списка контактов), LIST (вывод списка команд), HELP (вывод справки по работе приложения).

                case LIST:
                    System.out.println("Список доступных команд:\n\tREAD\tREADALL\n\tCREATE\n\tUPDATE\n\tDELETE\n\tLIST\n\tEXIT");
                    break;
                case HELP:
                    System.out.println("Данная программа является реализацией телефонной книги для\nзаписи контактов в текстовый файл.\n\n" +
                            "Вы можете создавать новые контакты при помощи команды CREATE.\n" +
                            "\tБудет создан контакт с полями:\n\t\tИмя,\n\t\tФамилия\n\t\tНомер телефона\n" +
                            "Для внесения изменений Вы можете использовать команду UPDATE,\n" +
                            "а для удаления - DELETE.\n" +
                            "Для вывода контактов в виде списка используйте команду READALL.\n" +
                            "По одному - READ.\n\n" +
                            "Для выхода из программы наберите команду EXIT.\n");
                    break;
                case READALL:
                    List<User> users = userController.readAllUsers();
                    for (User user : users) { System.out.println(user); };
                    break;

В метод promt добавлены строковые методы, чтобы не зависеть от введенного кейса букв и паразитных пробелов.
      in.nextLine().toUpperCase(Locale.ROOT).replaceAll(" ", "").trim();
Из минусов подобного решения - контакты теперь тоже сохраняются только заглавными буквами.

Также вынесен код из Main в класс Preparation:

public class Preparation {
    private Preparation() {
        createDB();
        GBRepository repository = new UserRepository(DB_PATH);
        UserController controller = new UserController(repository);
        UserDialogue phoneBook = new UserDialogue(controller);
        phoneBook.run();
    }
    public static void run(){
        new Preparation();
    }
}

В Main'е теперь осталось только:

package notebook;
import static notebook.Preparation.run;
public class Main {
    public static void main(String[] args) {
        run();
    }
}

Вдальнейшем планирую организовать отдельную проверку введенных значений для полей, их более приятный на вид вывод на экран, 
