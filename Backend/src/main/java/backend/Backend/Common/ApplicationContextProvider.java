package backend.Backend.Common;


import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class ApplicationContextProvider implements ApplicationContextAware {
    private static ApplicationContext context;
    private static String activeProfile;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        context = applicationContext;
        Environment env = context.getEnvironment();
        String[] activeProfiles = env.getActiveProfiles();

        // If you want just one active profile or a default
        activeProfile = activeProfiles.length > 0 ? activeProfiles[0] : "dev";

    }

    public static ApplicationContext getApplicationContext() {
        return context;
    }

    /**
     * provide spring profile to non spring components
     * @return String
     */
    public static String getSpringActiveProfile() {
        return activeProfile;
    }
}
