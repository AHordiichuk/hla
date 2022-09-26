package prjctr.transaction;

import com.zaxxer.hikari.util.IsolationLevel;
import java.sql.Connection;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import prjctr.transaction.pehomenas.Phenomena;
import prjctr.transaction.pehomenas.PhenomenaTest;

import static com.zaxxer.hikari.util.IsolationLevel.TRANSACTION_READ_COMMITTED;
import static java.util.function.Function.identity;

@SpringBootTest
@ActiveProfiles("test")
class TransactionTest {

    @Autowired
    private DataSource mysql;

    @Autowired
    private DataSource percona;

    @Autowired
    private ApplicationContext context;

    @Autowired
    private Collection<PhenomenaTest> phenomenaTests;

    @CsvSource({
        // MySQL
        "TRANSACTION_READ_UNCOMMITTED, DIRTY_READ, MYSQL",
        "TRANSACTION_READ_UNCOMMITTED, LOST_UPDATE, MYSQL",
        "TRANSACTION_READ_UNCOMMITTED, NON_REPEATABLE_READ, MYSQL",
        "TRANSACTION_READ_UNCOMMITTED, PHANTOM_READ, MYSQL",

        "TRANSACTION_READ_COMMITTED, DIRTY_READ, MYSQL",
        "TRANSACTION_READ_COMMITTED, LOST_UPDATE, MYSQL",
        "TRANSACTION_READ_COMMITTED, NON_REPEATABLE_READ, MYSQL",
        "TRANSACTION_READ_COMMITTED, PHANTOM_READ, MYSQL",

        "TRANSACTION_REPEATABLE_READ, DIRTY_READ, MYSQL",
        "TRANSACTION_REPEATABLE_READ, LOST_UPDATE, MYSQL",
        "TRANSACTION_REPEATABLE_READ, NON_REPEATABLE_READ, MYSQL",
        "TRANSACTION_REPEATABLE_READ, PHANTOM_READ, MYSQL",

//        "TRANSACTION_SERIALIZABLE, DIRTY_READ, MYSQL",
//        "TRANSACTION_SERIALIZABLE, LOST_UPDATE, MYSQL",
//        "TRANSACTION_SERIALIZABLE, NON_REPEATABLE_READ, MYSQL",
//        "TRANSACTION_SERIALIZABLE, PHANTOM_READ, MYSQL",

        // Percona
        "TRANSACTION_READ_UNCOMMITTED, DIRTY_READ, PERCONA",
        "TRANSACTION_READ_UNCOMMITTED, LOST_UPDATE, PERCONA",
        "TRANSACTION_READ_UNCOMMITTED, NON_REPEATABLE_READ, PERCONA",
        "TRANSACTION_READ_UNCOMMITTED, PHANTOM_READ, PERCONA",

        "TRANSACTION_READ_COMMITTED, DIRTY_READ, PERCONA",
        "TRANSACTION_READ_COMMITTED, LOST_UPDATE, PERCONA",
        "TRANSACTION_READ_COMMITTED, NON_REPEATABLE_READ, PERCONA",
        "TRANSACTION_READ_COMMITTED, PHANTOM_READ, PERCONA",

        "TRANSACTION_REPEATABLE_READ, DIRTY_READ, PERCONA",
        "TRANSACTION_REPEATABLE_READ, LOST_UPDATE, PERCONA",
        "TRANSACTION_REPEATABLE_READ, NON_REPEATABLE_READ, PERCONA",
        "TRANSACTION_REPEATABLE_READ, PHANTOM_READ, PERCONA",

//        "TRANSACTION_SERIALIZABLE, DIRTY_READ, PERCONA",
//        "TRANSACTION_SERIALIZABLE, LOST_UPDATE, PERCONA",
//        "TRANSACTION_SERIALIZABLE, NON_REPEATABLE_READ, PERCONA",
//        "TRANSACTION_SERIALIZABLE, PHANTOM_READ, PERCONA"

        // Postgres
        "TRANSACTION_READ_UNCOMMITTED, DIRTY_READ, POSTGRES",
        "TRANSACTION_READ_UNCOMMITTED, LOST_UPDATE, POSTGRES",
        "TRANSACTION_READ_UNCOMMITTED, NON_REPEATABLE_READ, POSTGRES",
        "TRANSACTION_READ_UNCOMMITTED, PHANTOM_READ, POSTGRES",

        "TRANSACTION_READ_COMMITTED, DIRTY_READ, POSTGRES",
        "TRANSACTION_READ_COMMITTED, LOST_UPDATE, POSTGRES",
        "TRANSACTION_READ_COMMITTED, NON_REPEATABLE_READ, POSTGRES",
        "TRANSACTION_READ_COMMITTED, PHANTOM_READ, POSTGRES",

        "TRANSACTION_REPEATABLE_READ, DIRTY_READ, POSTGRES",
        "TRANSACTION_REPEATABLE_READ, LOST_UPDATE, POSTGRES",
        "TRANSACTION_REPEATABLE_READ, NON_REPEATABLE_READ, POSTGRES",
        "TRANSACTION_REPEATABLE_READ, PHANTOM_READ, POSTGRES",

    })
    @ParameterizedTest(name = "{0} : {1} : {2}")
    void testPhenomena(final IsolationLevel isolationLevel, final Phenomena phenomena, final String dataSourceName) {
        final var dataSource = getDataSource(dataSourceName);

        prepareDb(dataSource);

        final var connectionA = getConnection(dataSource, isolationLevel);
        final var connectionB = getConnection(dataSource, isolationLevel);
        final var phenomenaTest = getPhenomenaTest(phenomena);

        phenomenaTest.test(connectionA, connectionB);
    }

    @SneakyThrows
    private void prepareDb(final DataSource dataSource) {
        final var connection = getConnection(dataSource, TRANSACTION_READ_COMMITTED);
        connection.createStatement().execute("DROP TABLE IF EXISTS users");
        connection.createStatement().execute("CREATE TABLE users (id INT PRIMARY KEY, name TEXT NOT NULL, age INT NOT NULL)");
        connection.createStatement().execute("INSERT INTO users (id, name, age) VALUES (1, 'Joe', 20), (2, 'Jill', 25)");
        connection.commit();
        connection.close();
    }

    private DataSource getDataSource(final String dataSourceName) {
        return context.getBean(dataSourceName.toLowerCase(), DataSource.class);
    }

    private PhenomenaTest getPhenomenaTest(final Phenomena phenomena) {
        final var testsByPhenomena = phenomenaTests.stream()
            .collect(Collectors.toMap(PhenomenaTest::supportedPhenomena, identity()));

        return Optional.ofNullable(testsByPhenomena.get(phenomena))
            .orElseThrow(() -> new IllegalArgumentException("There is no test for phenomena " + phenomena));
    }

    @SneakyThrows
    private Connection getConnection(final DataSource dataSource, final IsolationLevel isolationLevel) {
        final var connection = dataSource.getConnection();
        connection.setAutoCommit(false);
        connection.setTransactionIsolation(isolationLevel.getLevelId());
        return connection;
    }
}