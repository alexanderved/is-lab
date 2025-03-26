package client;

import java.io.IOException;
import java.net.InetAddress;

import client.controller.Runner;
import client.controller.commands.AddCommand;
import client.controller.commands.AddIfMaxCommand;
import client.controller.commands.AddIfMinCommand;
import client.controller.commands.AverageOfEmployeesCountCommand;
import client.controller.commands.ClearCommand;
import client.controller.commands.EmptyCommand;
import client.controller.commands.ExecuteScriptCommand;
import client.controller.commands.ExitCommand;
import client.controller.commands.FilterStartsWithFullNameCommand;
import client.controller.commands.GeneralCommandParser;
import client.controller.commands.HelpCommand;
import client.controller.commands.InfoCommand;
import client.controller.commands.PrintFieldDescendingAnnualTurnoverCommand;
import client.controller.commands.RemoveByIDCommand;
import client.controller.commands.RemoveLowerCommand;
import client.controller.commands.ShowCommand;
import client.controller.commands.UpdateCommand;
import client.network.TCPStreamServerConnection;
import client.view.View;
import client.view.cli.Cli;

public class Main {
    public static void main(String[] args) {
        View view = new Cli();
        GeneralCommandParser cmdParser = new GeneralCommandParser();

        TCPStreamServerConnection conn;
        try {
            conn = new TCPStreamServerConnection(InetAddress.getLoopbackAddress(), 6789);
        } catch (IOException e) {
            view.error(e.getMessage());

            return;
        }

        cmdParser.register("", new EmptyCommand.Parser());
        cmdParser.register("help", new HelpCommand.Parser());
        cmdParser.register("info", new InfoCommand.Parser());
        cmdParser.register("show", new ShowCommand.Parser());
        cmdParser.register("add", new AddCommand.Parser());
        cmdParser.register("update", new UpdateCommand.Parser());
        cmdParser.register("remove_by_id", new RemoveByIDCommand.Parser());
        cmdParser.register("clear", new ClearCommand.Parser());
        cmdParser.register("execute_script", new ExecuteScriptCommand.Parser());
        cmdParser.register("exit", new ExitCommand.Parser());
        cmdParser.register("add_if_max", new AddIfMaxCommand.Parser());
        cmdParser.register("add_if_min", new AddIfMinCommand.Parser());
        cmdParser.register("remove_lower", new RemoveLowerCommand.Parser());
        cmdParser.register("average_of_employees_count",
            new AverageOfEmployeesCountCommand.Parser());
        cmdParser.register("filter_starts_with_full_name",
            new FilterStartsWithFullNameCommand.Parser());
        cmdParser.register("print_field_descending_annual_turnover",
            new PrintFieldDescendingAnnualTurnoverCommand.Parser());

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                conn.close();
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }));

        Runner runner = new Runner(view, conn, cmdParser);

        runner.run();
    }
}
