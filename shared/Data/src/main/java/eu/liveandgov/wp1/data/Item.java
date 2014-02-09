package eu.liveandgov.wp1.data;

/**
 * <p>Abstract stream item</p>
 * Created by Lukas Härtel on 08.02.14.
 */
public class Item<Data> {
    /**
     * Type of the item
     */
    public final String type;

    /**
     * Header of the item
     */
    public final Header header;

    /**
     * Content of the item
     */
    public final Data data;

    /**
     * Creates the item with the given values
     */
    public Item(String type, Header header, Data data) {
        this.type = type;
        this.header = header;
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Item item = (Item) o;

        if (data != null ? !data.equals(item.data) : item.data != null) return false;
        if (header != null ? !header.equals(item.header) : item.header != null) return false;
        if (type != null ? !type.equals(item.type) : item.type != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (header != null ? header.hashCode() : 0);
        result = 31 * result + (data != null ? data.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Item{" +
                "type='" + type + '\'' +
                ", header=" + header +
                ", data=" + data +
                '}';
    }
}
