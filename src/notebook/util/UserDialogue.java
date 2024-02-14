package notebook.util;

import notebook.controller.UserController;
import notebook.model.User;
import notebook.model.repository.impl.UserRepository;
import notebook.util.Commands;

import java.util.List;
import java.util.Locale;
import java.util.Scanner;

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

    //Character.toUpperCase(in.charAt(0)) + in.substring(1);
}
