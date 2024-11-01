package axoncqrses.axon.querymodel;

import axoncqrses.querymodel.InMemoryOrdersEventHandler;
import axoncqrses.querymodel.OrdersEventHandler;

public class InMemoryOrdersEventHandlerUnitTest extends AbstractOrdersEventHandlerUnitTest {

    @Override
    protected OrdersEventHandler getHandler() {
        return new InMemoryOrdersEventHandler(emitter);
    }
}
