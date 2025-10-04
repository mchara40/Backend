package backend.Backend.Common;

import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.util.Date;

@Slf4j
public class APIResults {
    protected static final String HOSTNAME;
    protected String error;
    protected Object results;
    protected EnumApiResultStatus status;
    protected String debugInfo;

    protected String activeSpringProfile = ApplicationContextProvider.getSpringActiveProfile();

    java.text.DateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    java.text.DateFormat dferror = new java.text.SimpleDateFormat("yyyyMMddHHmmssSSS");

    static {
        String tmp = "";
        try {
            tmp = InetAddress.getLocalHost().getHostName();
        } catch (Exception ex) {

        }
        HOSTNAME = tmp;
    }



    public static final APIResults successApiResults(final Object results) {
        return new APIResults(EnumApiResultStatus.SUCCESS, results);
    }

    public static final APIResults failedApiResults(final Throwable t) {


        final APIResults results = new APIResults(EnumApiResultStatus.ERROR);
        final String errorCounter = results.dferror.format(new Date(System.currentTimeMillis()));


        if(!results.activeSpringProfile.equals("prod")){
            return results.setError(t.getMessage());
        } else {
            //dont reveal the error message in production
            return results.setError("Error number [" +errorCounter+ "] occurred");
        }

    }

    public static final APIResults failedApiResults(final String errorMessage) {

        final APIResults results = new APIResults(EnumApiResultStatus.ERROR);
        final String errorCounter = results.dferror.format(new Date(System.currentTimeMillis()));


        //dont reveal the error message in production
        return results.setError(errorMessage);

    }

    public APIResults() {

    }

    public APIResults(final EnumApiResultStatus status) {
        super();
        this.status = status;
        this.error = null;
        this.results = null;
    }

    public APIResults(final EnumApiResultStatus status, final Object results) {
        super();
        this.status = status;
        this.error = null;
        this.results = results;
    }

    /**
     * sets the results in error with the passed string the error reason
     *
     * @param error
     */
    public APIResults(final String error) {
        super();
        this.status = EnumApiResultStatus.ERROR;
        this.error = error;
    }

    public String getError() {
        return error;
    }

    public APIResults setError(String error) {
        this.error = error;
        return this;
    }

    public Object getResults() {
        return results;
    }

    public APIResults setResults(Object results) {
        this.results = results;
        return this;
    }

    public EnumApiResultStatus getStatus() {
        return status;
    }

    public APIResults setStatus(EnumApiResultStatus status) {
        this.status = status;
        return this;
    }

    public String getStrTimestamp() {

        return df.format(new Date( System.currentTimeMillis()));
    }

    public String getHostname() {

        if(!activeSpringProfile.equals("prod")){
            return HOSTNAME + " - " + activeSpringProfile;
        } else {
            return null;
        }
    }


    public String getDebugInfo() {
        if(!activeSpringProfile.equals("prod")){
            return debugInfo;
        } else {
            return null;
        }

    }

    public void setDebugInfo(String debugInfo) {
        this.debugInfo = debugInfo;
    }
}
