import java.util.logging.*;
import java.io.IOException;

/**
 * Centralized logging utility using java.util.logging.
 * Logs to both console and a log file.
 */
public class LoggerUtil {

    private static final Logger logger = Logger.getLogger("ShopBillingSystem");
    private static boolean initialized = false;

    /**
     * Initialize logger with console and file handlers.
     */
    public static void init() {
        if (initialized) return;
        try {
            logger.setLevel(Level.ALL);

            // Console handler
            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setLevel(Level.INFO);
            consoleHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(consoleHandler);

            // File handler
            FileHandler fileHandler = new FileHandler("shop_billing.log", true);
            fileHandler.setLevel(Level.ALL);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);

            logger.setUseParentHandlers(false);
            initialized = true;
            logger.info("Logger initialized successfully");
        } catch (IOException e) {
            System.err.println("Failed to initialize logger: " + e.getMessage());
        }
    }

    /**
     * Returns the application logger instance.
     */
    public static Logger getLogger() {
        if (!initialized) init();
        return logger;
    }
}
