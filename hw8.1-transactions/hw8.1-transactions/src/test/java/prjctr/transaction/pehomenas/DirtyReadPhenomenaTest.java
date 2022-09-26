package prjctr.transaction.pehomenas;

import java.sql.Connection;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static org.assertj.core.api.Assertions.assertThat;
import static prjctr.transaction.pehomenas.Phenomena.DIRTY_READ;

@Slf4j
@Component
class DirtyReadPhenomenaTest extends AbstractPhenomenaTest {

    DirtyReadPhenomenaTest() {
        super(DIRTY_READ);
    }

    @Override
    @SneakyThrows
    void doTest(final Connection connectionA, final Connection connectionB) {
        final var initialUser = select(connectionA, "SELECT age FROM users WHERE id = 1");
        assertThat(initialUser).first().hasFieldOrPropertyWithValue("age", 20);
        log.info("A read initial user with age 20");

        update(connectionB, "UPDATE users SET age = 21 WHERE id = 1");
        log.info("B update user age to 21");

        final var uncommittedUser = select(connectionA, "SELECT age FROM users WHERE id = 1");
        assertThat(uncommittedUser).first().hasFieldOrPropertyWithValue("age", 21);
        log.info("A read uncommitted user with age 21");

        rollback(connectionB);

        final var rolledBackUser = select(connectionA, "SELECT age FROM users WHERE id = 1");
        assertThat(rolledBackUser).first().hasFieldOrPropertyWithValue("age", 20);;

        commit(connectionA);
    }
}