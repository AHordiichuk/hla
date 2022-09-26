package prjctr.transaction.pehomenas;

import java.sql.Connection;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import static org.assertj.core.api.Assertions.assertThat;
import static prjctr.transaction.pehomenas.Phenomena.LOST_UPDATE;

@Component
class LostUpdatePhenomenaTest extends AbstractPhenomenaTest {

    LostUpdatePhenomenaTest() {
        super(LOST_UPDATE);
    }

    @Override
    @SneakyThrows
    void doTest(final Connection connectionA, final Connection connectionB) {
        final var initialUser = select(connectionA, "SELECT age FROM users WHERE id = 1");
        assertThat(initialUser).first().hasFieldOrPropertyWithValue("age", 20);

        update(connectionB, "UPDATE users SET age = age + 1 WHERE id = 1");

        final var updatedUser = select(connectionB, "SELECT age FROM users WHERE id = 1");
        assertThat(updatedUser).first().hasFieldOrPropertyWithValue("age", 21);

        commit(connectionB);

        connectionA.createStatement().execute("UPDATE users SET age = 21 WHERE id = 1");

        final var lostUpdatedUser = select(connectionA, "SELECT age FROM users WHERE id = 1");
        assertThat(lostUpdatedUser).first().hasFieldOrPropertyWithValue("age", 21);;

        commit(connectionA);
    }
}