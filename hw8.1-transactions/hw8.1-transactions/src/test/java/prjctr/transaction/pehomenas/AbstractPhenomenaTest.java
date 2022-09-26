package prjctr.transaction.pehomenas;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@RequiredArgsConstructor
public abstract class AbstractPhenomenaTest implements PhenomenaTest {

    private final Phenomena phenomena;

    @Override
    public Phenomena supportedPhenomena() {
        return phenomena;
    }

    @Override
    public void test(final Connection connectionA, final Connection connectionB) {
        try {
            doTest(connectionA, connectionB);
        } catch (final RuntimeException e) {
            closeConnection(connectionA);
            closeConnection(connectionB);
        } finally {
            closeConnection(connectionA);
            closeConnection(connectionB);
        }
    }

    abstract void doTest(final Connection connectionA, final Connection connectionB);

    @SneakyThrows
    private void closeConnection(final Connection connection) {
        if (!connection.isClosed()) {
            connection.close();
        }
    }

    @SneakyThrows
    protected static void rollback(final Connection connection) {
        connection.rollback();
    }

    @SneakyThrows
    protected static void commit(final Connection connection) {
        connection.commit();
    }

    @SneakyThrows
    protected static List<Map<String, Object>> select(final Connection connection, final String query) {
        final var resultSet = connection.createStatement().executeQuery(query);
        return getRecords(resultSet);
    }

    @SneakyThrows
    protected static void update(final Connection connection, final String query) {
        connection.createStatement().execute(query);
    }

    @SneakyThrows
    private static List<Map<String, Object>> getRecords(final ResultSet resultSet) {
        final var records = new ArrayList<Map<String, Object>>();
        final var resultSetMataData = resultSet.getMetaData();
        final var columnsCount = resultSetMataData.getColumnCount();

        while (resultSet.next()) {
            final var record = new LinkedHashMap<String, Object>();

            for (int i = 1; i <= columnsCount; i++) {
                record.put(resultSetMataData.getColumnLabel(i), resultSet.getObject(i));
            }

            records.add(record);
        }

        return records;
    }
}