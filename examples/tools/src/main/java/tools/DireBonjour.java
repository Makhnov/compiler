package tools;

import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

public class DireBonjour extends SayHello {
    private static final Logger LOG = Logger.getLogger(DireBonjour.class);

    @Override
    public String getMessage() {
        // LOG.trace("Appel de getMessage() dans DireBonjour");
        return "Bonjour";
    }

    @Override
    public String getOtherMessage() {
        // LOG.trace("Appel de getOtherMessage() dans DireBonjour");
        return "Au revoir";
    }

    @Override
    public String getBothMessages() {
        return getMessage() + "\n" + getOtherMessage();
    }

    @Override
    public void sayIt() {
        // LOG.debug("Je suis à deux doigts de le dire");
        System.out.println(getMessage());
    }

    @Override
    public void sayOtherMessage() {
        // LOG.trace("Je vais vous dire l'autre chose");
        System.out.println(getOtherMessage());
    }

    @Override
    public void sayItTo(String name) {
        // LOG.debug("Je vais le dire à " + name);
        Validate.notNull(name);
        System.out.println("Bonjour, " + name);
    }
}
