package leveleight.artus.com.myapplication;

import java.util.Map;

/**
 * The persistence object of couch database
 *
 * @author Artem Emelyanov
 */
public interface ICouchPersitance<T extends Object> {

    /**
     * Stores object to database
     */
    void store();

    /**
     * Gets accessible properties
     *
     * @return The properties
     */
    Map<String, Object> properties();

    /**
     * Restores object from database
     */
    T restore();


}
