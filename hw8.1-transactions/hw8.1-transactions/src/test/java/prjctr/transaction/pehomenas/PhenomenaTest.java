package prjctr.transaction.pehomenas;

import java.sql.Connection;

public interface PhenomenaTest {

    Phenomena supportedPhenomena();

    void test(Connection connectionA, Connection connectionB);

}