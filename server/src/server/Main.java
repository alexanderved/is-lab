package server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

import server.application.CollectionManager;
import server.application.IDGenerator;
import server.application.IncrementalIDGenerator;
import server.controller.Runner;
import server.controller.executors.AddIfMaxRequestExecutor;
import server.controller.executors.AddIfMinRequestExecutor;
import server.controller.executors.AddRequestExecutor;
import server.controller.executors.AverageOfEmployeesCountRequestExecutor;
import server.controller.executors.ClearRequestExecutor;
import server.controller.executors.FilterStartsWithFullNameRequestExecutor;
import server.controller.executors.GeneralRequestExecutor;
import server.controller.executors.InfoRequestExecutor;
import server.controller.executors.PrintFieldDescendingAnnualTurnoverRequestExecutor;
import server.controller.executors.RemoveByIDRequestExecutor;
import server.controller.executors.RemoveLowerRequestExecutor;
import server.controller.executors.ShowRequestExecutor;
import server.controller.executors.UpdateRequestExecutor;
import server.exceptions.RepositoryAccessDeniedException;
import server.exceptions.RepositoryDataCorruptedException;
import server.network.providers.TCPChannelConnectionProvider;
import server.repository.xml.XmlRepository;

public class Main {
    public static final int PORT = 4891;
    public static final Logger logger = Logger.getGlobal();
    
    public static void main(String[] args) {
        String repoPath = System.getenv("REPO_PATH");
        if (repoPath == null) {
            System.err.println("Файл репозитория не найден."
                + " Необходимо установить переменную окружения `REPO_PATH`.");

            return;
        }

        SocketAddress addr = new InetSocketAddress(InetAddress.getLoopbackAddress(), PORT);
        TCPChannelConnectionProvider provider;
        try {
            provider = new TCPChannelConnectionProvider(addr);
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.toString(), e);

            return;
        }

        IDGenerator idGen = new IncrementalIDGenerator();
        XmlRepository repo = new XmlRepository(repoPath);
        CollectionManager collection;
        try {
            collection = new CollectionManager(idGen, repo);
        } catch (RepositoryAccessDeniedException | RepositoryDataCorruptedException e) {
            logger.log(Level.SEVERE, e.toString(), e);

            return;
        }
        GeneralRequestExecutor reqExecutor = new GeneralRequestExecutor();

        Runner runner = new Runner(provider, reqExecutor, collection);

        reqExecutor.register("info", new InfoRequestExecutor());
        reqExecutor.register("show", new ShowRequestExecutor());
        reqExecutor.register("add", new AddRequestExecutor());
        reqExecutor.register("update", new UpdateRequestExecutor());
        reqExecutor.register("remove_by_id", new RemoveByIDRequestExecutor());
        reqExecutor.register("clear", new ClearRequestExecutor());
        reqExecutor.register("add_if_max", new AddIfMaxRequestExecutor());
        reqExecutor.register("add_if_min", new AddIfMinRequestExecutor());
        reqExecutor.register("remove_lower", new RemoveLowerRequestExecutor());
        reqExecutor.register("average_of_employees_count",
            new AverageOfEmployeesCountRequestExecutor());
        reqExecutor.register("filter_starts_with_full_name",
            new FilterStartsWithFullNameRequestExecutor());
        reqExecutor.register("print_field_descending_annual_turnover",
            new PrintFieldDescendingAnnualTurnoverRequestExecutor());

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                collection.save();
            } catch (RepositoryAccessDeniedException e) {
                logger.log(Level.SEVERE, e.toString(), e);
            }

            try {
                provider.close();
            } catch (IOException e) {
                logger.log(Level.SEVERE, e.toString(), e);
            }
        }));

        runner.run();
    }
}
