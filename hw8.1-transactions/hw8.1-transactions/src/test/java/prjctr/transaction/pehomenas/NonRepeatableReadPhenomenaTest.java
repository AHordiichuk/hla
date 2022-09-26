package prjctr.transaction.pehomenas;

import java.sql.Connection;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import static org.assertj.core.api.Assertions.assertThat;
import static prjctr.transaction.pehomenas.Phenomena.NON_REPEATABLE_READ;

@Component
class NonRepeatableReadPhenomenaTest extends AbstractPhenomenaTest {

    NonRepeatableReadPhenomenaTest() {
        super(NON_REPEATABLE_READ);
    }

    @Override
    @SneakyThrows
    void doTest(final Connection connectionA, final Connection connectionB) {
        final var initialUser = select(connectionA, "SELECT * FROM users WHERE id = 1");
        assertThat(initialUser).first().hasFieldOrPropertyWithValue("age", 20);

        update(connectionB, "UPDATE users SET age = 21 WHERE id = 1");

        final var committedUser = select(connectionB, "SELECT age FROM users WHERE id = 1");
        assertThat(committedUser).first().hasFieldOrPropertyWithValue("age", 21);

        commit(connectionB);

        final var repeatableUser = select(connectionA, "SELECT age FROM users WHERE id = 1");
        assertThat(repeatableUser).first().hasFieldOrPropertyWithValue("age", 21);

        commit(connectionA);
    }
}