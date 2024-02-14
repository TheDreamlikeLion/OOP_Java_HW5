package notebook;

import notebook.controller.UserController;
import notebook.model.repository.GBRepository;
import notebook.model.repository.impl.UserRepository;
import notebook.util.UserDialogue;

import static notebook.util.DBConnector.DB_PATH;
import static notebook.util.DBConnector.createDB;

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
