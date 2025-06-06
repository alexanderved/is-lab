package server;

import org.springframework.beans.factory.BeanCreationException;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import server.service.auth.Hasher;
import server.service.auth.SHA512Hasher;
import server.repository.OrganizationDAO;
import server.repository.UserDAO;
import server.repository.postgresql.PostgreSQLContext;
import server.repository.postgresql.PostgreSQLOrganizationDAO;
import server.repository.postgresql.PostgreSQLUserDAO;

import java.security.NoSuchAlgorithmException;

@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Bean
    public Hasher hasher() {
        try {
            return new SHA512Hasher();
        } catch (NoSuchAlgorithmException e) {
            throw new BeanCreationException("hasher", "Не удалось создать Hasher", e);
        }
    }

    @Bean
    public PostgreSQLContext postgreSQLContext() {
        return new PostgreSQLContext("jdbc:postgresql:studs",
                "alex", "password");
    }

    @Bean
    public UserDAO userDAO() {
        return new PostgreSQLUserDAO(postgreSQLContext());
    }

    @Bean
    public OrganizationDAO organizationDAO() {
        return new PostgreSQLOrganizationDAO(postgreSQLContext());
    }
    
//    public static void main(String[] args)
//            throws UnknownHostException, RepositoryAccessDeniedException, NoSuchAlgorithmException {
//        PostgreSQLRepository repo;
//        if (args.length > 0) {
//            repo = new PostgreSQLRepository(args[0], args[1], args[2]);
//        } else {
//            repo = new PostgreSQLRepository("pg:5432/studs");
//        }
//
//        TaskExecutor executor = new TaskExecutor(
//                (task) -> new Thread(task).start(), (task) -> new Thread(task).start());
//
//        SocketAddress addr = new InetSocketAddress(InetAddress.getByAddress(new byte[]{0, 0, 0, 0}), PORT);
//        TCPChannelConnectionProvider provider;
//        try {
//            provider = new TCPChannelConnectionProvider(executor, addr);
//        } catch (IOException e) {
//            Logger.getGlobal().error(e.toString(), e);
//
//            return;
//        }
//
//        UserManager userManager = new UserManager(new SHA512Hasher(), repo);
//
//        IDGenerator idGen = repo.createIDGenerator();
//        CollectionManager collection;
//        try {
//            collection = new CollectionManager(idGen, repo, userManager);
//        } catch (RepositoryAccessDeniedException | RepositoryDataCorruptedException e) {
//            Logger.getGlobal().error(e.toString(), e);
//
//            return;
//        }
//        GeneralRequestExecutor reqExecutor = new GeneralRequestExecutor();
//        ClientResponder responder = new ClientResponder(provider, reqExecutor, new TaskThreadFactory() {
//            private final ExecutorService forkJoinExecutor = new ForkJoinPool();
//
//            @Override
//            public void submit(Runnable task) {
//                forkJoinExecutor.submit(task);
//            }
//        });
//
//        Runner runner = new Runner(responder, userManager, collection);
//
//        reqExecutor.register("info", new InfoRequestExecutor());
//        reqExecutor.register("show", new ShowRequestExecutor());
//        reqExecutor.register("add", new AddRequestExecutor());
//        reqExecutor.register("update", new UpdateRequestExecutor());
//        reqExecutor.register("remove_by_id", new RemoveByIDRequestExecutor());
//        reqExecutor.register("clear", new ClearRequestExecutor());
//        reqExecutor.register("add_if_max", new AddIfMaxRequestExecutor());
//        reqExecutor.register("add_if_min", new AddIfMinRequestExecutor());
//        reqExecutor.register("remove_lower", new RemoveLowerRequestExecutor());
//        reqExecutor.register("average_of_employees_count",
//            new AverageOfEmployeesCountRequestExecutor());
//        reqExecutor.register("filter_starts_with_full_name",
//            new FilterStartsWithFullNameRequestExecutor());
//        reqExecutor.register("print_field_descending_annual_turnover",
//            new PrintFieldDescendingAnnualTurnoverRequestExecutor());
//        reqExecutor.register("login", new LoginRequestExecutor());
//        reqExecutor.register("register", new RegisterRequestExecutor());
//
//        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
//            try {
//                provider.close();
//            } catch (IOException e) {
//                Logger.getGlobal().error(e.toString(), e);
//            }
//
//            try {
//                repo.close();
//            } catch (SQLException e) {
//                Logger.getGlobal().error(e.toString(), e);
//            }
//        }));
//
//        runner.run();
//    }
}
