package ru.netology.qa.diplom.data;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.DriverManager;

public class DataSQL {
    private final static String[] tableNames = {"credit_request_entity", "payment_entity", "order_entity"};
    private static final String userDB = "app";
    private static final String passwordDB = "pass";
    static QueryRunner runner = new QueryRunner();
    private static String dbUrl = System.getProperty("db.url"); //для запуска из консоли

    private DataSQL() {
    }

    @SneakyThrows
    public static long getRowsAmountFrom(String tableName) {
        var rowsAmountQuery = "SELECT COUNT(*) FROM " + tableName + ";";
        try (var conn = DriverManager.getConnection(dbUrl, userDB, passwordDB)) {
            return runner.query(conn, rowsAmountQuery, new ScalarHandler<>());
        }
    }

    @SneakyThrows
    public static Payment getLastEntryFromPaymentsTable() {
        var statusQuery = "SELECT status, amount FROM payment_entity WHERE transaction_id = (SELECT payment_id FROM order_entity ORDER BY created DESC LIMIT 1);";
        Payment payment;
        ResultSetHandler<Payment> resultHandler = new BeanHandler<Payment>(Payment.class);

        var conn = DriverManager.getConnection(dbUrl, userDB, passwordDB);
        payment = runner.query(conn, statusQuery, resultHandler);
        return payment;
    }

    @SneakyThrows
    public static String getLastStatusFromCreditsTable() {
        var statusQuery = "SELECT status FROM credit_request_entity WHERE bank_id = (SELECT credit_id FROM order_entity ORDER BY created DESC LIMIT 1);";
        String status;

        var conn = DriverManager.getConnection(dbUrl, userDB, passwordDB);
        status = runner.query(conn, statusQuery, new ScalarHandler<>());
        conn.close(); // Закрытие соединения после выполнения запроса

        return status;
    }

    @SneakyThrows
    public static void cleanDatabase() {
        var conn = DriverManager.getConnection(dbUrl, userDB, passwordDB);

        for (int i = 0; i < tableNames.length; i++) {
            runner.execute(conn, "DELETE FROM " + tableNames[i] + ";");
        }

        conn.close(); // Закрытие соединения после выполнения всех операций
    }

    @Getter
    @Setter
    public static class Payment {
        private String status;
        private String amount;
    }
}

