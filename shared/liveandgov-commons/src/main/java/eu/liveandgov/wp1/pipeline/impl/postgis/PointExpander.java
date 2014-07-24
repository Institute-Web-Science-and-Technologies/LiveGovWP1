package eu.liveandgov.wp1.pipeline.impl.postgis;

import eu.liveandgov.wp1.data.Triple;
import eu.liveandgov.wp1.pipeline.Pipeline;
import org.postgis.Geometry;
import org.postgis.PGgeometry;
import org.postgis.Point;
import org.postgresql.geometric.PGpoint;
import org.postgresql.util.PGobject;

import java.sql.SQLException;
import java.util.*;

@Deprecated
/**
 * <p>Point expander takes a map enriched with PGobjects or </p>
 * Created by Lukas Härtel on 18.03.14.
 */
public class PointExpander extends Pipeline<Map<String, ?>, Map<String, ?>> {
    private final Set<Triple<String, String, String>> expansions = new HashSet<Triple<String, String, String>>();

    public void addExpansion(String point, String outLon, String outLat) {
        expansions.add(Triple.create(point, outLon, outLat));
    }

    public void removeExpansion(String point, String outLon, String outLat) {
        expansions.remove(Triple.create(point, outLon, outLat));
    }

    public void removeExpansions(String point) {
        final Iterator<Triple<String, String, String>> ti = expansions.iterator();
        while (ti.hasNext()) {
            final Triple<String, String, String> t = ti.next();

            if (t.left == null) {
                if (point == null) ti.remove();
            } else if (t.left.equals(point)) {
                ti.remove();
            }
        }
    }


    @Override
    public void push(Map<String, ?> map) {
        if (expansions.isEmpty()) {
            produce(map);
        } else {
            final Map<String, Object> newMap = new HashMap<String, Object>(map);

            for (Triple<String, String, String> expansion : expansions) {
                // Only expand existing fields
                if (map.containsKey(expansion.left))
                    try {
                        // Source object
                        final Object p = map.get(expansion.left);
                        //Geometry to output
                        final Geometry geometry;

                        // Discriminate type
                        if (p instanceof PGgeometry)
                            geometry = ((PGgeometry) p).getGeometry();
                        else if (p instanceof PGobject)
                            geometry = PGgeometry.geomFromString(((PGobject) p).getValue());
                        else if (p instanceof String)
                            geometry = PGgeometry.geomFromString((String) p);
                        else
                            throw new IllegalArgumentException();

                        // Get first point of the geometry
                        final Point point = geometry.getFirstPoint();

                        // Put new fields
                        newMap.put(expansion.center, point.getX());
                        newMap.put(expansion.right, point.getY());

                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
            }

            // Produce new map
            produce(newMap);
        }
    }
}
