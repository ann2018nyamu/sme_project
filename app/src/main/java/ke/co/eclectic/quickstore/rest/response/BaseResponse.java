package ke.co.eclectic.quickstore.rest.response;


/**
 * The type Base response.
 */
public class BaseResponse {
    /**
     * The Error.
     */
    private String error;
    /**
     * The Message.
     */
    private String message;

    private String curtime="";
    private String status = "";




    /**
     * Instantiates a new Base response.
     */
    public BaseResponse() {
    }

    /**
     * Gets error.
     *
     * @return the error
     */
    public String getError() {
        return error;
    }

    /**
     * Gets message.
     *
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Gets status.
     *
     * @return the status
     */
    public String getStatus() {

        return status;
    }

    /**
     * Gets curtime.
     *
     * @return the curtime
     */
    public String getCurtime() {
        return curtime;
    }

}
