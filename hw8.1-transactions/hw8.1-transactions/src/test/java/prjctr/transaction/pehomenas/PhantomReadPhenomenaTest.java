package prjctr.transaction.pehomenas;

import java.sql.Connection;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import static org.assertj.core.api.Assertions.assertThat;
import static prjctr.transaction.pehomenas.Phenomena.PHANTOM_READ;

@Component
class PhantomReadPhenomenaTest extends AbstractPhenomenaTest {

    PhantomReadPhenomenaTest() {
        super(PHANTOM_READ);
    }

    @Override
    @SneakyThrows
    void doTest(final Connection connectionA, final Connection connectionB) {
        final var initialUsers = select(connectionA, "SELECT * FROM users WHERE age BETWEEN 10 AND 30");
        assertThat(initialUsers).hasSize(2);

        update(connectionB, "INSERT INTO users(id, name, age) VALUES (3, 'Bob', 27)");

        final var updatedUsers = select(connectionB, "SELECT * FROM users WHERE age BETWEEN 10 AND 30");
        assertThat(updatedUsers).hasSize(3);

        commit(connectionB);

        final var phantomUsers = select(connectionA, "SELECT * FROM users WHERE age BETWEEN 10 AND 30");
        assertThat(phantomUsers).hasSize(3);

        commit(connectionA);
    }
}