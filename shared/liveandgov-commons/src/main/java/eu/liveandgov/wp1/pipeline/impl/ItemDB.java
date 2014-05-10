package eu.liveandgov.wp1.pipeline.impl;

import eu.liveandgov.wp1.data.Item;
import eu.liveandgov.wp1.forwarding.Provider;
import eu.liveandgov.wp1.forwarding.Receiver;
import eu.liveandgov.wp1.forwarding.impl.ItemForwarding;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Lukas Härtel on 10.05.2014.
 */
public class ItemDB extends DB<Item, Item> {
    public ItemDB(ScheduledExecutorService scheduledExecutorService, long closeConnectionDelay, TimeUnit closeConnectionUnit, long insertDelay, TimeUnit insertUnit, long closeSelectDelay, TimeUnit closeSelectUnit, String url, String username, String password, String table) {
        super(scheduledExecutorService, closeConnectionDelay, closeConnectionUnit, insertDelay, insertUnit, closeSelectDelay, closeSelectUnit, url, username, password, table);
    }

    public ItemDB(ScheduledExecutorService scheduledExecutorService, String url, String username, String password, String table) {
        super(scheduledExecutorService, url, username, password, table);
    }

    @Override
    protected Item transform(Item item) {
        return item;
    }

    @Override
    protected Item read(final ResultSet resultSet) throws SQLException {
        return ItemForwarding.ITEM_FORWARDING.unForward(new Provider() {

            private String lastComponent;

            private Object lastValue;

            private Object get(String component) {
                try {
                    if (Objects.equals(lastComponent, component))
                        return lastValue;

                    lastComponent = component;
                    lastValue = null;

                    ResultSetMetaData metaData = resultSet.getMetaData();

                    for (int i = 1; i <= metaData.getColumnCount(); i++)
                        if (Objects.equals(component, metaData.getColumnName(i))) {
                            lastValue = resultSet.getObject(i);
                            break;
                        }
                    return lastValue;
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }


            @Override
            public boolean contains(String component) {

                return get(component) != null;
            }

            @Override
            public Object provide(String component) {
                return get(component);
            }
        });
    }

    @Override
    protected void write(Item item, final PreparedStatement insertStatement) throws SQLException {
        ItemForwarding.ITEM_FORWARDING.forward(item, new Receiver() {
            private String lastComponent;

            private int lastIndex;

            private void put(String component, Object x) {
                try {
                    if (Objects.equals(lastComponent, component)) {
                        if (lastIndex != 0)
                            insertStatement.setObject(lastIndex, x);

                        return;
                    }

                    lastComponent = component;
                    lastIndex = 0;

                    ResultSetMetaData metaData = insertStatement.getMetaData();

                    for (int i = 1; i <= metaData.getColumnCount(); i++)
                        if (Objects.equals(component, metaData.getColumnName(i))) {
                            lastIndex = i;
                            break;
                        }

                    if (lastIndex != 0)
                        insertStatement.setObject(lastIndex, x);

                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }


            @Override
            public void receive(String component, Object item) {
                put(component, item);
            }
        });
    }
}
