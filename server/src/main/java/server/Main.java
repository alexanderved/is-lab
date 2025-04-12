package server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.*;

import server.application.CollectionManager;
import server.application.IDGenerator;
import server.controller.ClientResponder;
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
import server.logger.Logger;
import server.network.connections.TaskExecutor;
import server.network.providers.TCPChannelConnectionProvider;
import server.repository.postgresql.PostgreSQLRepository;
import server.utils.TaskThreadFactory;

public class Main {
    public static final int PORT = 4891;
    
    public static void main(String[] args)
            throws UnknownHostException, RepositoryAccessDeniedException {
        PostgreSQLRepository repo = new PostgreSQLRepository(
                "localhost:5432/studs", "alex", "");

        TaskExecutor executor = new TaskExecutor(
                (task) -> new Thread(task).start(), (task) -> new Thread(task).start());

        SocketAddress addr = new InetSocketAddress(InetAddress.getByAddress(new byte[]{0, 0, 0, 0}), PORT);
        TCPChannelConnectionProvider provider;
        try {
            provider = new TCPChannelConnectionProvider(executor, addr);
        } catch (IOException e) {
            Logger.getGlobal().error(e.toString(), e);

            return;
        }

        IDGenerator idGen = repo.createIDGenerator();
        CollectionManager collection;
        try {
            collection = new CollectionManager(idGen, repo);
        } catch (RepositoryAccessDeniedException | RepositoryDataCorruptedException e) {
            Logger.getGlobal().error(e.toString(), e);

            return;
        }
        GeneralRequestExecutor reqExecutor = new GeneralRequestExecutor();
        ClientResponder responder = new ClientResponder(provider, reqExecutor, new TaskThreadFactory() {
            private final ExecutorService forkJoinExecutor = new ForkJoinPool();

            @Override
            public void submit(Runnable task) {
                forkJoinExecutor.submit(task);
            }
        });

        Runner runner = new Runner(responder, collection);

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
                provider.close();
            } catch (IOException e) {
                Logger.getGlobal().error(e.toString(), e);
            }

            try {
                repo.close();
            } catch (SQLException e) {
                Logger.getGlobal().error(e.toString(), e);
            }
        }));

        runner.run();
    }
}
