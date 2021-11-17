package au.com.alm.bdd.config;

import org.aeonbits.owner.ConfigFactory;

public class ALMBDDConfigHolder {
    /* The instance of ALMBDDConfig */
    private static final ALMBDDConfig INSTANCE = ConfigFactory.create(ALMBDDConfig.class);

    // Singleton behavior
    private ALMBDDConfigHolder() {
    }

    /**
     * Gets the instance of ALMBDDConfig.
     *
     * @return the instance of ALMBDDConfig.
     */
    public static ALMBDDConfig getInstance() {
        return INSTANCE;
    }
}
